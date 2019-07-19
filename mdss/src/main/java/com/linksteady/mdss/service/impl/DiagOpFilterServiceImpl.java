package com.linksteady.mdss.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.mdss.config.KpiCacheManager;
import com.linksteady.mdss.dao.CommonSelectMapper;
import com.linksteady.mdss.domain.DiagFilterDataCollector;
import com.linksteady.mdss.domain.DiagHandleInfo;
import com.linksteady.mdss.domain.DiagResultInfo;
import com.linksteady.mdss.domain.KpiConfigInfo;
import com.linksteady.mdss.service.DiagOpService;
import com.linksteady.mdss.vo.DimJoinVO;
import com.linksteady.mdss.vo.KpiSqlTemplateVO;
import com.linksteady.mdss.vo.TemplateFilter;
import com.linksteady.mdss.vo.TemplateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 诊断处理过程 - 过滤
 * @author  huang
 */
@Slf4j
@Service
public class DiagOpFilterServiceImpl implements DiagOpService {

    @Autowired
    DiagOpCommonServiceImpl diagOpCommonService;

    @Autowired
    CommonSelectMapper commonSelectMapper;

    /**
     * 过滤条件的拆解
     * @param diagHandleInfo 封装操作信息的类
     * @return
     */
    @Override
    public DiagResultInfo process(DiagHandleInfo diagHandleInfo) {
        DiagResultInfo resultInfo=new DiagResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        KpiConfigInfo kpiConfigInfo= KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode);
        String kpiName=kpiConfigInfo.getKpiName();
        String formatType=kpiConfigInfo.getValueFormat();

        double kpiValue=0d;

        KpiSqlTemplateVO kpiSqlTemplate;
        //判断用户选择的维度中是否含有品牌、SPU 如果有，则获取 从明细查询的模板
        if(diagOpCommonService.isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase());
        }

        //判断是否拿到了模板
        if(null!=kpiSqlTemplate&& !StringUtils.isBlank(kpiSqlTemplate.getSqlTemplate())) {
            //构造参数 填充到模板中
            StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

            //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
            List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());
            TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters);

            //$DATE_RANGE$
            String dataRange=diagOpCommonService.buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

            stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",dataRange);

            if(log.isDebugEnabled())
            {
                log.debug("诊断ID={},LEVEL_ID={},过滤的SQL为{}",diagHandleInfo.getDiagId(),diagHandleInfo.getKpiLevelId(),stringTemplate.render());
            }

            //发送SQL到数据库中执行，并获取结果
            DiagFilterDataCollector diagFilterDataCollector=commonSelectMapper.selectOnlyDoubleValue(stringTemplate.render());

            if(null!=diagFilterDataCollector&&diagFilterDataCollector.getValue()!=null)
            {
                kpiValue=diagFilterDataCollector.getValue();
            }
        }

        //对结果进行格式化

        resultInfo.setKpiCode(kpiCode);
        resultInfo.setKpiName(kpiName);
        resultInfo.setKpiValue(diagOpCommonService.valueFormat(kpiValue,formatType));
        return resultInfo;
    }

    /**
     *
     * @param driverTableName  驱动表名称
     * @param filterInfo  所选的维度信息列表
     * @return TemplateResult 返回构建好的join信息和where信息
     */
    private TemplateResult buildWhereInfo(String driverTableName,List<TemplateFilter> filterInfo)
    {
        Map<String, DimJoinVO> dimJoin=KpiCacheManager.getInstance().getDimJoinList().row(driverTableName);

        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        Set dimTableAliasSet= Sets.newHashSet();

        StringBuilder join=new StringBuilder();
        StringBuilder filter=new StringBuilder();
        Joiner joiner = Joiner.on(",").skipNulls();

        for(TemplateFilter templateFilter :filterInfo)
        {
            //清空
            filter.setLength(0);
            join.setLength(0);

            //通过dimcode获取到其背后的信息
            DimJoinVO dimJoinVO=dimJoin.get(templateFilter.getDimCode());

            //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
            if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
            {
                //加入到判断重复的set中
                dimTableAliasSet.add(dimJoinVO.getDimTableAlias());
                //加入到拼接队列
                join.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());
            }

            //where条件
            List<String> values= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(templateFilter.getDimValues());
            //字符串类型
            if("STRING".equals(dimJoinVO.getDimWhereType()))
            {
                if(values.size()==1)
                {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append("='").append(values.get(0)).append("'");
                }else
                {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values.stream().map(a->"'"+a+"'").toArray())).append(")");
                }
            }else if("NUMBER".equals(dimJoinVO.getDimWhereType())) {
                //数字类型
                if (values.size() == 1) {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append("=").append(values.get(0));
                } else {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values)).append(")");
                }
            }

            filters.append(filter.toString());
            joins.append(join.toString());
        }

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
    }
}
