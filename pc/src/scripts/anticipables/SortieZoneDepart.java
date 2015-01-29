package scripts.anticipables;

import java.util.ArrayList;

import astar.arc.PathfindingNodes;
import exceptions.FinMatchException;
import exceptions.ScriptHookException;
import exceptions.SerialConnexionException;
import exceptions.UnableToMoveException;
import hook.HookFactory;
import robot.RobotChrono;
import scripts.Script;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * Faux script. Permet de sortir de la zone de départ.
 * Pas de version. Exécutée dès le début du match.
 * Permet de calculer une stratégie à la sortie de ce "script"
 * @author pf
 *
 */

public class SortieZoneDepart extends Script {

	public SortieZoneDepart(HookFactory hookgenerator, Config config, Log log)
	{
		super(hookgenerator, config, log);
	}

	@Override
	public ArrayList<PathfindingNodes> getVersions(GameState<RobotChrono> state) {
		ArrayList<PathfindingNodes> out = new ArrayList<PathfindingNodes>();
		out.add(PathfindingNodes.POINT_DEPART);
		return out;
	}

	@Override
	protected void execute(PathfindingNodes id_version, GameState<?> state)
			throws UnableToMoveException, SerialConnexionException,
			FinMatchException, ScriptHookException
	{
		state.robot.tourner(Math.PI);
		state.robot.avancer(500); // TODO vérifier distance
	}

	@Override
	protected void termine(GameState<?> state) throws SerialConnexionException, FinMatchException, ScriptHookException
	{}
	
}