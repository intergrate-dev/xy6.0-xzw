package com.founder.amuc.jobs;

import com.founder.amuc.score.PeriodProcessor;
import com.founder.e5.context.E5Exception;
import com.founder.e5.scheduler.BaseJob;

/**
 * 
 * @author Gong Lijie
 * 2014-6-6
 */
public class ScorePeriodJob extends BaseJob {
	@Override
	protected void execute() throws E5Exception {
		PeriodProcessor processor = new PeriodProcessor();
		processor.process();
	}
}
