package com.linksteady.operate.domain;

import javax.persistence.*;

@Table(name = "UO_STATE_JUDGE")
public class StateJudge {
    /**
     * 新客户_新客期_销售贡献率
     */
    @Column(name = "NN_PROFIT_RATE")
    private Short nnProfitRate;

    /**
     * 新客户_新客期_销售额增长率
     */
    @Column(name = "NN_SALES_RATE")
    private Short nnSalesRate;

    /**
     * 新客户_新客期_用户数增长率
     */
    @Column(name = "NN_UCT_RATE")
    private Short nnUctRate;

    /**
     * 新客户_新客期用户数占比
     */
    @Column(name = "NN_UCT")
    private Short nnUct;

    /**
     * 新客户_成长期_销售贡献率
     */
    @Column(name = "NI_PROFIT_RATE")
    private Short niProfitRate;

    /**
     * 新客户_成长期_销售额增长率
     */
    @Column(name = "NI_SALES_RATE")
    private Short niSalesRate;

    /**
     * 新客户_成长期_用户数增长率
     */
    @Column(name = "NI_UCT_RATE")
    private Short niUctRate;

    /**
     * 新客户_成长期用户数占比
     */
    @Column(name = "NI_UCT")
    private Short niUct;

    /**
     * 老客户_成熟期_销售贡献率
     */
    @Column(name = "OM_PROFIT_RATE")
    private Short omProfitRate;

    /**
     * 老客户_成熟期_销售额增长率
     */
    @Column(name = "OM_SALES_RATE")
    private Short omSalesRate;

    /**
     * 老客户_成熟期_用户数增长率
     */
    @Column(name = "OM_UCT_RATE")
    private Short omUctRate;

    /**
     * 老客户_成熟期用户数占比
     */
    @Column(name = "OM_UCT")
    private Short omUct;

    /**
     * 老客户_衰退期_销售贡献率
     */
    @Column(name = "OD_PROFIT_RATE")
    private Short odProfitRate;

    /**
     * 老客户_衰退期_销售额增长率
     */
    @Column(name = "OD_SALES_RATE")
    private Short odSalesRate;

    /**
     * 老客户_衰退期_用户数增长率
     */
    @Column(name = "OD_UCT_RATE")
    private Short odUctRate;

    /**
     * 老客户_衰退期用户数占比
     */
    @Column(name = "OD_UCT")
    private Short odUct;

    /**
     * 老客户_流失期_销售贡献率
     */
    @Column(name = "OL_PROFIT_RATE")
    private Short olProfitRate;

    /**
     * 老客户_流失期_销售额增长率
     */
    @Column(name = "OL_SALES_RATE")
    private Short olSalesRate;

    /**
     * 老客户_流失期_用户数增长率
     */
    @Column(name = "OL_UCT_RATE")
    private Short olUctRate;

    /**
     * 老客户_流失期用户数占比
     */
    @Column(name = "OL_UCT")
    private Short olUct;

    /**
     * 结论提示文本
     */
    @Column(name = "CONCLUSION")
    private String conclusion;

    /**
     * 月
     */
    @Column(name = "MONTH_ID")
    private Short monthId;

    /**
     * 获取新客户_新客期_销售贡献率
     *
     * @return NN_PROFIT_RATE - 新客户_新客期_销售贡献率
     */
    public Short getNnProfitRate() {
        return nnProfitRate;
    }

    /**
     * 设置新客户_新客期_销售贡献率
     *
     * @param nnProfitRate 新客户_新客期_销售贡献率
     */
    public void setNnProfitRate(Short nnProfitRate) {
        this.nnProfitRate = nnProfitRate;
    }

    /**
     * 获取新客户_新客期_销售额增长率
     *
     * @return NN_SALES_RATE - 新客户_新客期_销售额增长率
     */
    public Short getNnSalesRate() {
        return nnSalesRate;
    }

    /**
     * 设置新客户_新客期_销售额增长率
     *
     * @param nnSalesRate 新客户_新客期_销售额增长率
     */
    public void setNnSalesRate(Short nnSalesRate) {
        this.nnSalesRate = nnSalesRate;
    }

    /**
     * 获取新客户_新客期_用户数增长率
     *
     * @return NN_UCT_RATE - 新客户_新客期_用户数增长率
     */
    public Short getNnUctRate() {
        return nnUctRate;
    }

    /**
     * 设置新客户_新客期_用户数增长率
     *
     * @param nnUctRate 新客户_新客期_用户数增长率
     */
    public void setNnUctRate(Short nnUctRate) {
        this.nnUctRate = nnUctRate;
    }

    /**
     * 获取新客户_新客期用户数占比
     *
     * @return NN_UCT - 新客户_新客期用户数占比
     */
    public Short getNnUct() {
        return nnUct;
    }

    /**
     * 设置新客户_新客期用户数占比
     *
     * @param nnUct 新客户_新客期用户数占比
     */
    public void setNnUct(Short nnUct) {
        this.nnUct = nnUct;
    }

    /**
     * 获取新客户_成长期_销售贡献率
     *
     * @return NI_PROFIT_RATE - 新客户_成长期_销售贡献率
     */
    public Short getNiProfitRate() {
        return niProfitRate;
    }

    /**
     * 设置新客户_成长期_销售贡献率
     *
     * @param niProfitRate 新客户_成长期_销售贡献率
     */
    public void setNiProfitRate(Short niProfitRate) {
        this.niProfitRate = niProfitRate;
    }

    /**
     * 获取新客户_成长期_销售额增长率
     *
     * @return NI_SALES_RATE - 新客户_成长期_销售额增长率
     */
    public Short getNiSalesRate() {
        return niSalesRate;
    }

    /**
     * 设置新客户_成长期_销售额增长率
     *
     * @param niSalesRate 新客户_成长期_销售额增长率
     */
    public void setNiSalesRate(Short niSalesRate) {
        this.niSalesRate = niSalesRate;
    }

    /**
     * 获取新客户_成长期_用户数增长率
     *
     * @return NI_UCT_RATE - 新客户_成长期_用户数增长率
     */
    public Short getNiUctRate() {
        return niUctRate;
    }

    /**
     * 设置新客户_成长期_用户数增长率
     *
     * @param niUctRate 新客户_成长期_用户数增长率
     */
    public void setNiUctRate(Short niUctRate) {
        this.niUctRate = niUctRate;
    }

    /**
     * 获取新客户_成长期用户数占比
     *
     * @return NI_UCT - 新客户_成长期用户数占比
     */
    public Short getNiUct() {
        return niUct;
    }

    /**
     * 设置新客户_成长期用户数占比
     *
     * @param niUct 新客户_成长期用户数占比
     */
    public void setNiUct(Short niUct) {
        this.niUct = niUct;
    }

    /**
     * 获取老客户_成熟期_销售贡献率
     *
     * @return OM_PROFIT_RATE - 老客户_成熟期_销售贡献率
     */
    public Short getOmProfitRate() {
        return omProfitRate;
    }

    /**
     * 设置老客户_成熟期_销售贡献率
     *
     * @param omProfitRate 老客户_成熟期_销售贡献率
     */
    public void setOmProfitRate(Short omProfitRate) {
        this.omProfitRate = omProfitRate;
    }

    /**
     * 获取老客户_成熟期_销售额增长率
     *
     * @return OM_SALES_RATE - 老客户_成熟期_销售额增长率
     */
    public Short getOmSalesRate() {
        return omSalesRate;
    }

    /**
     * 设置老客户_成熟期_销售额增长率
     *
     * @param omSalesRate 老客户_成熟期_销售额增长率
     */
    public void setOmSalesRate(Short omSalesRate) {
        this.omSalesRate = omSalesRate;
    }

    /**
     * 获取老客户_成熟期_用户数增长率
     *
     * @return OM_UCT_RATE - 老客户_成熟期_用户数增长率
     */
    public Short getOmUctRate() {
        return omUctRate;
    }

    /**
     * 设置老客户_成熟期_用户数增长率
     *
     * @param omUctRate 老客户_成熟期_用户数增长率
     */
    public void setOmUctRate(Short omUctRate) {
        this.omUctRate = omUctRate;
    }

    /**
     * 获取老客户_成熟期用户数占比
     *
     * @return OM_UCT - 老客户_成熟期用户数占比
     */
    public Short getOmUct() {
        return omUct;
    }

    /**
     * 设置老客户_成熟期用户数占比
     *
     * @param omUct 老客户_成熟期用户数占比
     */
    public void setOmUct(Short omUct) {
        this.omUct = omUct;
    }

    /**
     * 获取老客户_衰退期_销售贡献率
     *
     * @return OD_PROFIT_RATE - 老客户_衰退期_销售贡献率
     */
    public Short getOdProfitRate() {
        return odProfitRate;
    }

    /**
     * 设置老客户_衰退期_销售贡献率
     *
     * @param odProfitRate 老客户_衰退期_销售贡献率
     */
    public void setOdProfitRate(Short odProfitRate) {
        this.odProfitRate = odProfitRate;
    }

    /**
     * 获取老客户_衰退期_销售额增长率
     *
     * @return OD_SALES_RATE - 老客户_衰退期_销售额增长率
     */
    public Short getOdSalesRate() {
        return odSalesRate;
    }

    /**
     * 设置老客户_衰退期_销售额增长率
     *
     * @param odSalesRate 老客户_衰退期_销售额增长率
     */
    public void setOdSalesRate(Short odSalesRate) {
        this.odSalesRate = odSalesRate;
    }

    /**
     * 获取老客户_衰退期_用户数增长率
     *
     * @return OD_UCT_RATE - 老客户_衰退期_用户数增长率
     */
    public Short getOdUctRate() {
        return odUctRate;
    }

    /**
     * 设置老客户_衰退期_用户数增长率
     *
     * @param odUctRate 老客户_衰退期_用户数增长率
     */
    public void setOdUctRate(Short odUctRate) {
        this.odUctRate = odUctRate;
    }

    /**
     * 获取老客户_衰退期用户数占比
     *
     * @return OD_UCT - 老客户_衰退期用户数占比
     */
    public Short getOdUct() {
        return odUct;
    }

    /**
     * 设置老客户_衰退期用户数占比
     *
     * @param odUct 老客户_衰退期用户数占比
     */
    public void setOdUct(Short odUct) {
        this.odUct = odUct;
    }

    /**
     * 获取老客户_流失期_销售贡献率
     *
     * @return OL_PROFIT_RATE - 老客户_流失期_销售贡献率
     */
    public Short getOlProfitRate() {
        return olProfitRate;
    }

    /**
     * 设置老客户_流失期_销售贡献率
     *
     * @param olProfitRate 老客户_流失期_销售贡献率
     */
    public void setOlProfitRate(Short olProfitRate) {
        this.olProfitRate = olProfitRate;
    }

    /**
     * 获取老客户_流失期_销售额增长率
     *
     * @return OL_SALES_RATE - 老客户_流失期_销售额增长率
     */
    public Short getOlSalesRate() {
        return olSalesRate;
    }

    /**
     * 设置老客户_流失期_销售额增长率
     *
     * @param olSalesRate 老客户_流失期_销售额增长率
     */
    public void setOlSalesRate(Short olSalesRate) {
        this.olSalesRate = olSalesRate;
    }

    /**
     * 获取老客户_流失期_用户数增长率
     *
     * @return OL_UCT_RATE - 老客户_流失期_用户数增长率
     */
    public Short getOlUctRate() {
        return olUctRate;
    }

    /**
     * 设置老客户_流失期_用户数增长率
     *
     * @param olUctRate 老客户_流失期_用户数增长率
     */
    public void setOlUctRate(Short olUctRate) {
        this.olUctRate = olUctRate;
    }

    /**
     * 获取老客户_流失期用户数占比
     *
     * @return OL_UCT - 老客户_流失期用户数占比
     */
    public Short getOlUct() {
        return olUct;
    }

    /**
     * 设置老客户_流失期用户数占比
     *
     * @param olUct 老客户_流失期用户数占比
     */
    public void setOlUct(Short olUct) {
        this.olUct = olUct;
    }

    /**
     * 获取结论提示文本
     *
     * @return CONCLUSION - 结论提示文本
     */
    public String getConclusion() {
        return conclusion;
    }

    /**
     * 设置结论提示文本
     *
     * @param conclusion 结论提示文本
     */
    public void setConclusion(String conclusion) {
        this.conclusion = conclusion == null ? null : conclusion.trim();
    }

    /**
     * 获取月
     *
     * @return MONTH_ID - 月
     */
    public Short getMonthId() {
        return monthId;
    }

    /**
     * 设置月
     *
     * @param monthId 月
     */
    public void setMonthId(Short monthId) {
        this.monthId = monthId;
    }
}