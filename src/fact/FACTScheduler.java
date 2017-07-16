/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.text.SimpleDateFormat;
import java.util.Date;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.CronTrigger;
import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import wrapper.FeedConnProperties;

/**
 *
 * @author Ravindra
 */
public class FACTScheduler {

    Scheduler sched;

    public void run() throws Exception {
        FeedConnProperties feedConnProperties = new FeedConnProperties();

        SchedulerFactory sf = new StdSchedulerFactory();
        sched = sf.getScheduler();
        Date runTime = evenMinuteDate(new Date());
        JobDetail job = newJob(FACTStartPoint.class).withIdentity(feedConnProperties.getFeedname(), "group1").build();

        CronTrigger trigger = newTrigger()
                .withIdentity(feedConnProperties.getFeedname(), "group1").startAt(new SimpleDateFormat(feedConnProperties.getDateFormat()).parse(feedConnProperties.getStartDate()))
                .withSchedule(cronSchedule(feedConnProperties.getCronTime()))
                .build();
        sched.scheduleJob(job, trigger);
        sched.start();

    }

    void stop() throws SchedulerException {

        sched.shutdown(true);
    }

}
