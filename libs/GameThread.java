import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
/*
 * Program flow:
 *
 * 1) Load world
 * 2) Start acceptors
 * 3) Wait for the given number of players to connect
 * 4) Randomize spawnpoints on the board
 * 4) Send initial GAMESTART
 * 5) Wait for players to select loadout
 * 6) Start ticking
 *
 */

public class GameThread {
    ConcurrentLinkedQueue<AIConnection> globalClients;
    int minUsers;
    int gameTimeoutSeconds;
    int roundTimeSeconds;
    GameState gamestate;
    World world;
    AtomicInteger readyUsers = new AtomicInteger(0); // for loadouts
    public GameThread(ConcurrentLinkedQueue<AIConnection> globalClientsArg,
		      int minUsersArg, int gameTimeoutSecondsArg, int roundTimeSecondsArg, World world){
	globalClients = globalClientsArg;
	minUsers = minUsersArg;
	gameTimeoutSeconds = gameTimeoutSecondsArg;
	roundTimeSeconds = roundTimeSecondsArg;
	//	gamestate = new GameState("testworld.skyportmap");
    }
    public void run(int gameSecondsTimeout){
	try {
	    Thread.sleep(500);
	}
	catch(InterruptedException e){}
	System.out.println("[GAMETHRD] waiting for " + minUsers + " users to connect");
	int waitIteration = 0;
	while(true){
	    waitIteration++;
	    try {
		Thread.sleep(500);
	    }
	    catch(InterruptedException e){}
	    // todo: prevent acceptor from accepting more clients than we want.
	    if(globalClients.size() == minUsers){
		System.out.println("[GAMETHRD] Got " + minUsers + " users, starting the round");
		break;
	    }
	}
	for(AIConnection conn: globalClients){
	    JSONObject o = conn.getNextMessage();
	    System.out.println("[GAMETHRD] stub: got new message");
	}
	System.out.println("[GAMETHRD] running game for " + gameTimeoutSeconds + " seconds.");
	gameMainloop();
    }
    public void gameMainloop(){
	System.out.println("[GAMETHRD] Sending initial gamestate");
	sendGamestate();
	letClientsThink();
	letClientsThink();
	letClientsThink();
	long startTime = System.nanoTime();
	long gtsAsLong = gameTimeoutSeconds;
	while(true){
	    long roundStartTime = System.nanoTime();
	    if((roundStartTime - startTime) > gtsAsLong*1000000000){
		System.out.println("[GAMETHRD] Time over!");
		System.exit(0);
	    }
	    System.out.println("[GAMETHRD] Sending gamestate...");
	    sendGamestate();
	    letClientsThink();
	    System.out.println("[GAMETHRD] Deadline! Processing actions...");
	    // processing actions here
	}
	
    }
    public void letClientsThink(){
	try {
	    Thread.sleep(roundTimeSeconds*1000);
	}
	catch (InterruptedException e){
	    System.out.println("INTTERUPTED!");
	}
    }
    public void sendGamestate(){
	
    }
}