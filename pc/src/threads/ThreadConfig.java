package threads;

import container.Container;
import container.Service;
import container.ServiceNames;
import utils.Config;
import utils.Log;

/**
 * S'occupe de la mise a jour de la config. Surveille config
 * @author pf
 *
 */

public class ThreadConfig extends Thread implements Service {

	protected Log log;
	protected Config config;
	private Container container;
	
	public ThreadConfig(Log log, Config config, Container container)
	{
		this.log = log;
		this.config = config;
		this.container = container;
	}

	@Override
	public void run()
	{
		while(!Config.stopThreads)
		{
			synchronized(config)
			{
				try {
					config.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			log.debug("Réveil de ThreadConfig");	
			
			for(ServiceNames name: ServiceNames.values())
			{
				Service service = container.getInstanciedService(name);
				if(service != null)
					service.updateConfig();
			}
		}

	}

	@Override
	public void updateConfig()
	{}

}