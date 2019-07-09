package com.linksteady.jobmanager.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.jobmanager.domain.JobLog;
import com.linksteady.jobmanager.service.JobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class JobLogController extends BaseController {

	@Autowired
	private JobLogService jobLogService;

	@RequestMapping("jobLog/list")
	@ResponseBody
	public Map<String, Object> jobLogList(QueryRequest request, JobLog log) {
		return super.selectByPageNumSize(request, () -> this.jobLogService.findAllJobLogs(log));
	}

	@Log("删除调度日志")
	@RequestMapping("jobLog/delete")
	@ResponseBody
	public ResponseBo deleteJobLog(String ids) {
		try {
			this.jobLogService.deleteBatch(ids);
			return ResponseBo.ok("删除调度日志成功！");
		} catch (Exception e) {
			log.error("删除调度日志失败", e);
			return ResponseBo.error("删除调度日志失败，请联系网站管理员！");
		}
	}
}
