/*
Copyright (C) 2013-2017 Pierre-François Gimenez

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package pathfinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import container.Service;
import container.dependances.HighPFClass;
import exceptions.PathfindingException;
import pathfinding.astar.AStarCourbe;
import pathfinding.chemin.CheminPathfinding;
import pathfinding.chemin.FakeCheminPathfinding;
import robot.Cinematique;
import robot.CinematiqueObs;
import robot.Speed;
import scripts.Script;
import scripts.ScriptDeposeMinerai;
import scripts.ScriptManager;
import utils.Log;

/**
 * Service qui contient les chemins précalculés
 * @author pf
 *
 */

public class PathCache implements Service, HighPFClass
{
	public static volatile boolean precompute = false;
	private Log log;
	private AStarCourbe astar;
	private CheminPathfinding realChemin;
	private FakeCheminPathfinding fakeChemin;
	
	/**
	 * Les chemins précalculés.
	 */
	public HashMap<KeyPathCache, LinkedList<CinematiqueObs>> paths;
	
	public PathCache(Log log, ScriptManager smanager, ChronoGameState chrono, AStarCourbe astar, CheminPathfinding realChemin, FakeCheminPathfinding fakeChemin) throws InterruptedException
	{
		this.fakeChemin = fakeChemin;
		this.realChemin = realChemin;
		this.log = log;
		Cinematique start = new Cinematique(200, 1800, Math.PI, true, 0, Speed.STANDARD.translationalSpeed); // TODO
		chrono.robot.setCinematique(start);
		this.astar = astar;
		paths = new HashMap<KeyPathCache, LinkedList<CinematiqueObs>>();
		if(!new File("paths/").exists())
			new File("paths/").mkdir();
		loadAll(smanager, chrono, start);
	}
	
	private void savePath(KeyPathCache k, List<CinematiqueObs> path)
	{
    	log.debug("Sauvegarde d'une trajectoire : "+k.toString());
        try {
            FileOutputStream fichier;
            ObjectOutputStream oos;

            fichier = new FileOutputStream("paths/"+k.toString()+".dat");
            oos = new ObjectOutputStream(fichier);
            oos.writeObject(path);
            oos.flush();
            oos.close();
        	log.debug("Sauvegarde terminée");
        }
        catch(IOException e)
        {
            log.critical("Erreur lors de la sauvegarde de la trajectoire ! "+e);
        }
	}
	
	/**
	 * Prépare un chemin
	 * @param cinematiqueInitiale
	 * @param s
	 * @param shoot
	 * @throws PathfindingException
	 * @throws InterruptedException 
	 */
	public void prepareNewPathToScript(KeyPathCache k) throws PathfindingException, InterruptedException
	{
		LinkedList<CinematiqueObs> path = paths.get(k);
		if(path == null)
		{
			k.s.setUpCercleArrivee();
			astar.initializeNewSearchToCircle(k.shoot, k.chrono);
			astar.process(fakeChemin);
		}
		else
			log.debug("Utilisation d'un trajet précalculé !");
	}
	
	/**
	 * Envoie le chemin précédemment préparé
	 * @throws InterruptedException 
	 */
	public void sendPreparedPath() throws InterruptedException, PathfindingException
	{
		/*
		 * Normalement, cette exception ne peut survenir que lors d'une replanification (donc pas là)
		 */
		synchronized(fakeChemin)
		{
			if(!fakeChemin.isReady())
				fakeChemin.wait();
			if(!fakeChemin.isReady()) // échec de la recherche TODO
				throw new PathfindingException();
			realChemin.add(fakeChemin.getPath());
		}
	}
	
	private LinkedList<CinematiqueObs> loadOrCompute(KeyPathCache k) throws InterruptedException, PathfindingException
	{
		LinkedList<CinematiqueObs> path;
		try {
			path = loadPath(k);
		} catch (ClassNotFoundException | IOException e1) {
			
			if(precompute)
			{
				log.warning("Calcul du chemin "+k);
				try {
					prepareNewPathToScript(k);
					path = fakeChemin.getPath();
					savePath(k, path);
				}
				finally
				{
					astar.stopContinuousSearch();
				}
			}
			else
			{
				throw new PathfindingException("Chargement du chemin "+k+" échoué : abandon.");
			}
		}
		return path;
	}
	
	private void loadAll(ScriptManager smanager, ChronoGameState chrono, Cinematique start) throws InterruptedException
	{
		Script depose = smanager.getScripts().get("DEPOSE");
		for(int i = 0; i < 2; i++)
		{
			smanager.reinit();
			KeyPathCache k = new KeyPathCache(chrono);
			while(smanager.hasNext())
			{
				k.chrono.robot.setCinematique(start);
				k.s = smanager.next();
				k.shoot = i == 0;

				if(k.s instanceof ScriptDeposeMinerai) // c'est particulier
					continue;
				
				log.debug(k);				
				LinkedList<CinematiqueObs> path;
				try {
					path = loadOrCompute(k);
				} catch (PathfindingException e1) {
					log.critical(e1);
					continue;
				}
				
				paths.put(k, path);
				// TODO : affichage à virer
				realChemin.clear();
				try {
					realChemin.add(path);
					Thread.sleep(2000);
				} catch (PathfindingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				// calcul du chemin retour
				k.chrono.robot.setCinematique(path.getLast());
				k.s = depose;
				for(int j = 0; j < 2; j++)
				{
					k.shoot = j == 0;
					LinkedList<CinematiqueObs> pathRetour;
					try {
						pathRetour = loadOrCompute(k);
					} catch (PathfindingException e1) {
						log.critical(e1);
						continue;
					}
					paths.put(k, pathRetour);
					// TODO : affichage à virer
					realChemin.clear();
					try {
						realChemin.add(pathRetour);
						Thread.sleep(2000);
					} catch (PathfindingException e) {
						e.printStackTrace();
					}
	
				}
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private LinkedList<CinematiqueObs> loadPath(KeyPathCache k) throws ClassNotFoundException, IOException
	{
    	log.debug("Chargement d'une trajectoire : "+k.toString());
        FileInputStream fichier = new FileInputStream("paths/"+k.toString()+".dat");
        ObjectInputStream ois = new ObjectInputStream(fichier);
        LinkedList<CinematiqueObs> path = (LinkedList<CinematiqueObs>) ois.readObject();
        ois.close();
        return path;
	}
	
	/**
	 * Le chemin a été entièrement parcouru.
	 */
	public synchronized void stopSearch()
	{
		astar.stopContinuousSearch();
	}
}
