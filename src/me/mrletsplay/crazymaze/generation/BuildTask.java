package me.mrletsplay.crazymaze.generation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import me.mrletsplay.crazymaze.main.CrazyMaze;

public class BuildTask {
	
	private List<Runnable> subTasks;
	private List<BukkitTask> tasks;
	private long expectedTimeTicks;

	public BuildTask(List<Runnable> subTasks) {
		this.subTasks = subTasks;
		this.tasks = new ArrayList<>();
	}
	
	public List<Runnable> getSubTasks() {
		return subTasks;
	}
	
	public void addSubTask(BuildTask task) {
		this.subTasks.addAll(task.getSubTasks());
	}
	
	public void addSubTasks(List<Runnable> subTasks) {
		this.subTasks.addAll(subTasks);
	}
	
	public void addSubTask(Runnable subTask) {
		this.subTasks.add(subTask);
	}
	
	public void execute(int offsetTicks, int tasksPerTick, Runnable thenRun) {
		subTasks.add(thenRun);
		
		List<BukkitTask> finalTasks = new ArrayList<>();
		long curr = offsetTicks, t = 0;
		for(Runnable run : subTasks) {
			finalTasks.add(Bukkit.getScheduler().runTaskLater(CrazyMaze.plugin, run, curr));
			t = (t + 1) % tasksPerTick;
			if(t == 0) curr++;
		}
		tasks.addAll(finalTasks);
		
		expectedTimeTicks = curr;
	}
	
	public void execute(int tasksPerTick, Runnable thenRun) {
		execute(0, tasksPerTick, thenRun);
	}
	
	public long getExpectedTimeTicks() {
		return expectedTimeTicks;
	}

	public void cancel() {
		tasks.forEach(t -> t.cancel());
	}
	
}
