package threads;

import utils.Config;
import utils.ConfigInfo;
import utils.Log;
import utils.Sleep;
import container.Service;

/**
 * Un thread passionnant qui dort pendant 90s
 * @author pf
 *
 */

public class ThreadFinMatch extends ThreadAvecStop implements Service {

	protected Log log;
	protected Config config;
	private volatile boolean matchDemarre = false;
	private int dureeMatch;

	public ThreadFinMatch(Log log, Config config)
	{
		this.log = log;
		this.config = config;
	}

	@Override
	public void run()
	{
		synchronized(this)
		{
			while(!matchDemarre)
			{
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log.debug("DEBUT DU MATCH!");
		Sleep.sleep(dureeMatch);
		config.set(ConfigInfo.FIN_MATCH, true);
		config.updateConfigServices();
		log.debug("Fin du Match !");
	}

	@Override
	public synchronized void updateConfig(Config config)
	{
		matchDemarre = config.getBoolean(ConfigInfo.MATCH_DEMARRE);
		notifyAll();
	}

	@Override
	public void useConfig(Config config)
	{
		dureeMatch = config.getInt(ConfigInfo.DUREE_MATCH_EN_S)*1000;
	}

}