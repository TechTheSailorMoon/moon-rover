package pathfinding;

import container.Service;
import robot.RobotChrono;
import strategie.GameState;
import utils.Log;
import utils.Config;
import enums.PathfindingNodes;
import enums.Speed;
import exceptions.FinMatchException;

 public class PathfindingArcManager implements ArcManager, Service {

	private int iterator, id_node_iterator;
//	private Config config;
//	private Log log;
	
	public PathfindingArcManager(Log log, Config config)
	{
//		this.log = log;
//		this.config = config;
		updateConfig();
	}
	
	@Override
	public double distanceTo(GameState<RobotChrono> state, Arc arc)
	{
		double temps_debut = state.robot.getTempsDepuisDebutMatch();
		try {
			state.robot.va_au_point_pathfinding((PathfindingNodes)arc, null);
			return state.robot.getTempsDepuisDebutMatch() - temps_debut;
		} catch (FinMatchException e) {
			return Double.MAX_VALUE;
		}
	}

	@Override
	public double heuristicCost(GameState<RobotChrono> state1, GameState<RobotChrono> state2)
	{
		// durée de rotation minimale
		double duree = state1.robot.calculateDelta(state1.robot.getPositionPathfinding().getOrientationFinale(state2.robot.getPositionPathfinding())) * Speed.BETWEEN_SCRIPTS.invertedRotationnalSpeed;
		// durée de translation minimale
		duree += state1.robot.getPositionPathfinding().distanceTo(state2.robot.getPositionPathfinding())*Speed.BETWEEN_SCRIPTS.invertedTranslationnalSpeed;
		return duree;
	}

	@Override
	public int getHash(GameState<RobotChrono> state) {
		return state.robot.getPositionPathfinding().ordinal();
	}

    @Override
    public PathfindingNodes next()
    {
    	return PathfindingNodes.values()[iterator];
    }
    
    @Override
    public boolean hasNext(GameState<RobotChrono> state)
    {
    	// TODO: accélérer
    	do {
    		iterator++;
    		// Ce point n'est pas bon si:
    		// c'est le noeud appelant (un noeud n'est pas son propre voisin)
    		// le noeud appelant et ce noeud ne peuvent être joints par une ligne droite

    	} while(iterator < PathfindingNodes.values().length
    			&& (iterator == id_node_iterator
    			|| !state.gridspace.isTraversable(PathfindingNodes.values()[id_node_iterator], PathfindingNodes.values()[iterator])));
    	return iterator != PathfindingNodes.values().length;
    }
    
    @Override
    public void reinitIterator(GameState<RobotChrono> gamestate)
    {
    	id_node_iterator = gamestate.robot.getPositionPathfinding().ordinal();
    	iterator = -1;
    }

	@Override
	public void updateConfig() {
	}

}
