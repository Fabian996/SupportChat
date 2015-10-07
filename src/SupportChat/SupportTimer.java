package de.Fabian996.SupportChat;

import java.util.UUID;

public class SupportTimer implements Runnable{

	private UUID uuid;
	private long timeout;
	private boolean running = true;
	
	protected SupportTimer(UUID uuid, long timeout){
		this.uuid = uuid;
		this.timeout = (System.currentTimeMillis() + timeout * 1000L);
	}
	
	public void end()
	{
		this.timeout = System.currentTimeMillis();
	}
	
	@Override
	public void run()
	{
		while (this.running)
			if(System.currentTimeMillis() >= this.timeout){
				this.running = false;
				SupportChat.removeTimer(this.uuid);
			}
	}

}
