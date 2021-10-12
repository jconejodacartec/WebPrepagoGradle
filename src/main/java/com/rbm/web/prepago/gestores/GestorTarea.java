package com.rbm.web.prepago.gestores;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.rbm.web.prepago.quartz.DepurarUsuariosQuartz;
import com.rbm.web.prepago.util.ConstantesWebPrepago;

public class GestorTarea {

	private static volatile GestorTarea gestorTarea = null;
	
	public GestorTarea() {
		
	}
	
	public static synchronized GestorTarea getInstance() {
		if (gestorTarea == null) {
			gestorTarea = new GestorTarea();
		}
		return gestorTarea;
	}
	
	public void programarTarea (String cron) throws SchedulerException{		
		this.programarAgendador(cron);
	}
	
	public void programarAgendador(String cron) throws SchedulerException {
		Trigger trigger =  getTriggerFromTaskSelected(ConstantesWebPrepago.JOB_NAME_PROCESO_DEPURAR_USUARIOS);
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		if(null != trigger){
			scheduler.unscheduleJob(trigger.getKey());
			scheduler.deleteJob(trigger.getJobKey());
		}
		trigger = null;

			JobDetail job = JobBuilder.newJob(DepurarUsuariosQuartz.class)
					.withIdentity(ConstantesWebPrepago.JOB_NAME_PROCESO_DEPURAR_USUARIOS, ConstantesWebPrepago.GROUP_NAME_PROCESO_DEPURAR_USUARIOS).build();

			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(ConstantesWebPrepago.TRIGGER_NAME_PROCESO_DEPURAR_USUARIOS, ConstantesWebPrepago.GROUP_NAME_PROCESO_DEPURAR_USUARIOS)
					.withSchedule(
							CronScheduleBuilder.cronSchedule(cron))
							.build();
			ejecutarProceso(scheduler,job,trigger);
	}
	
	public void ejecutarProceso(Scheduler scheduler, JobDetail job, Trigger trigger) throws SchedulerException{
		scheduler.start();
		if(!scheduler.checkExists(job.getKey()) && !scheduler.checkExists(trigger.getKey())){
			scheduler.scheduleJob(job, trigger);
		}else{
			scheduler.deleteJob(job.getKey());
			scheduler.scheduleJob(job, trigger);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Trigger getTriggerFromTaskSelected(String tareaSelected) throws SchedulerException{
		Trigger trigger = null;
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();

		groupNames : for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = jobKey.getName();

				if(jobName.equals(tareaSelected.trim()+"Job")){
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					trigger = triggers.get(0);
					break groupNames;
				}
			}
		}
		return trigger;
	}
	
	@SuppressWarnings("unchecked")
	public void eliminarTareas() throws SchedulerException{
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				Trigger trigger = triggers.get(0);
				scheduler.unscheduleJob(trigger.getKey());
				scheduler.deleteJob(trigger.getJobKey());
			}
		}
		scheduler.clear();
		scheduler.pauseAll();
		scheduler.shutdown();
	}
	
}
