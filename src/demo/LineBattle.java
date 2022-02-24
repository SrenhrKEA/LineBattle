package demo;

// NOTE: identisk med LineBattleV2.java, på nær at spillet ikke kører i et loop, men føres frem af metodekald og men afsluttes med metodekald exit(0).

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.System.exit;

public class LineBattle {
  //GLOBAL VARIABLES
  int soldiersPlayer = 25; // spillerens antal soldater
  int soldiersComputer = 25; // computerens antal soldater
  int ammoPlayer = 2500; // spillerens ammunition
  int ammoComputer = 2500; // computerens ammunition
  int positionPlayer; // spillerens position (-10..10)
  int positionComputer; // computerens position (-10..10)
  int counterTurns = 0; // tur counter
  int diceRollMovementPlayer; // spillerens terningekast ved bevægelse
  int diceRollMovementComputer; // computerens terningekast ved bevægelse
  int diceRollAttackPlayer; // spillerens terningekast ved angreb
  int diceRollAttackComputer; // computerens terningekast ved angreb
  final int NEW_AMMO = 250; // ny ammunition - konstant
  final int ATTACK_MULTIPLIER = 100; // multiplikator ved angreb - konstant
  final int MAX_CASUALTIES = 6; // maksimum antal dødstab - konstant
  Scanner in = new Scanner(System.in);
  Random random = new Random();

  public static void main(String[] args) {
    new LineBattle().execute();
  }

  //EXECUTE METHOD
  public void execute() {
    movementRoll();
    startingPositionAndTurns();
    scouting();
    switchTurns();
  }

  //MOVEMENT DICE ROLL
  public void movementRoll() {
    //Tilfældigt genereret terningekast for spilleren og computeren. Bruges når der tages et træk (frem eller tilbage).
    diceRollMovementPlayer = random.nextInt(6) + 1;
    diceRollMovementComputer = random.nextInt(6) + 1;
  }

  //STARTING POSITION AND TURNS.
  public void startingPositionAndTurns() {
    //Generere en startpositioner med spillets start (tur 0).
    if (counterTurns == 0) {
      positionPlayer = -10 + diceRollMovementPlayer;
      positionComputer = 10 - diceRollMovementComputer;
    }
    counterTurns = counterTurns + 1; // tur counter

    //Printer en linje ved endt tur.
    if (counterTurns > 1 && counterTurns % 2 != 0) {
      printLine();
      System.out.println("Fjenden har brugt sin tur og det er nu din tur.");
      printLine();
    }
  }

  //OBSERVATIONS - testet of virker for begge parter.
  public void scouting() {
    if (positionPlayer - positionComputer >= -2 && positionPlayer - positionComputer < 0) {
      if ((counterTurns % 2) != 0) {
        System.out.println("\nDine spejdere rapporterer at fjenden foran dig.");
        battlefieldMap();
      }
    } else if (positionPlayer - positionComputer <= 3 && positionPlayer - positionComputer > 0) {
      if ((counterTurns % 2) != 0) {
        System.out.println("\nDine spejdere rapporterer at fjenden bag dig.");
        battlefieldMap();
      }
    } else if (positionPlayer == positionComputer) {
      if ((counterTurns % 2) != 0) {
        System.out.println("\nDu står ansigt til ansigt med fjenden!! ANGRIB!!");
        battlefieldMap();
      }
    } else {
      if ((counterTurns % 2) != 0) {
        System.out.println("\nDine spejdere rapporterer at fjenden er ude af syne.");
      }
    }
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //PLAYER

  //REACTION MENU
  public void playerReaction() {
    troubleshooting();
    System.out.print("\nBestem hvad dine soldater skal gøre:");
    System.out.printf("\n%s\n%30s\n%s\n%29s\n%30s\n%s: ", "skal de:", "1. Angribe", "eller marchere:", "2. Fremad", "3. Tilbage", "Hvad vælger du?");
    String reaction = in.nextLine();
    switch (reaction) {
      case "1", "angrib", "Angrib" -> attackMovePlayer();
      case "2", "fremad", "Fremad" -> forwardMovePlayer();
      case "3", "tilbage", "Tilbage" -> backwardMovePlayer();
      default -> {
        System.out.println("Det virkede ikke. Prøv igen.");
        playerReaction();
      }
    }
  }

  //FORWARD MOVEMENT
  public void forwardMovePlayer() {
    if (diceRollMovementPlayer == 1 || diceRollMovementPlayer == 3 || diceRollMovementPlayer == 5) {
      positionPlayer = positionPlayer + 1;
    } else {
      positionPlayer = positionPlayer + 2;
    }
    if (positionPlayer > 9) {
      bombPlayer();
    }
    execute();
  }

  //BACKWARD MOVEMENT
  public void backwardMovePlayer() {
    int counterAmmo;
    if (diceRollMovementPlayer == 1 || diceRollMovementPlayer == 2) {
      positionPlayer = positionPlayer - 1;
      counterAmmo = 1;
    } else if (diceRollMovementPlayer == 3 || diceRollMovementPlayer == 4) {
      positionPlayer = positionPlayer - 2;
      counterAmmo = 2;
    } else {
      positionPlayer = positionPlayer - 3;
      counterAmmo = 3;
    }
    if (positionPlayer < -10) {
      positionPlayer = -10;
    }
    ammoPlayer = ammoPlayer + counterAmmo * NEW_AMMO;
    execute();
  }

  //ATTACK METHODS GROUP
  public void attackMovePlayer() {
    ammoCountPlayer();
    attackRollPlayer();
    attackCalculationsPlayer();
    manCountPlayer();
    execute();
  }

  //AMMUNITION COUNT
  public void ammoCountPlayer() {
    if (ammoPlayer < 1) {
      System.out.println("Dine soldater er løbet tør for ammunition og kan ikke angribe!");
      playerReaction();
    }
  }

  //ATTACK DICE ROLL
  public void attackRollPlayer() {
    diceRollAttackPlayer = random.nextInt(6) + 1;
  }

  //ATTACK CALCULATIONS
  public void attackCalculationsPlayer() {
    if (positionPlayer <= positionComputer) {
      int firePower = diceRollAttackPlayer * ATTACK_MULTIPLIER;
      if (ammoPlayer > firePower) {
        ammoPlayer = ammoPlayer - firePower;
        int distance = abs(positionPlayer - positionComputer);
        int casualties = MAX_CASUALTIES - distance;
        if (casualties < 0) {
          casualties = 0;
        }
        soldiersComputer = soldiersComputer - casualties;
        System.out.printf("\nDu har skudt %d af fjendens soldater!\n", casualties);
      } else {
        System.out.println("\nDine soldater har ikke nok ammunition til at angribe!");
        playerReaction();
      }
    } else {
      System.out.println("\nFjenden er bag dig. Du kan derfor ikke skyde dem!");
      playerReaction();
    }
  }

  //SOLDIERS COUNT
  public void manCountPlayer() {
    if (soldiersComputer < 1) {
      System.out.println("\nDu har skudt alle fjendens soldater! Tillykke du har vundet spillet!");
      exit(0); // NO MORE COMPUTER SOLDIERS - PLAYER WINS
    }
  }

  //MESSAGE - BOMB - PLAYER WINS
  public void bombPlayer() {
    System.out.println("\nDu har sprunget en bombe i fjendens lejer! Tillykke du har vundet spillet!");
    exit(0);
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //COMPUTER

  //REACTION MENU
  public void computerReaction() {
    if (positionPlayer <= positionComputer && positionPlayer-positionComputer>0) { //Computeren skyder hvis spilleren er foran dem og inden for 2 (i syne).
      attackMoveComputer();
    } else if (positionPlayer > positionComputer && positionComputer > -6) // computeren trækker sig tilbage hvis den spilleren kommer bag den, og den ikke selv er noget over halvvejs på spillerens banehalvdel.
      backwardMoveComputer();
    else { // default træk
      forwardMoveComputer();
    }
  }

  //FORWARD MOVEMENT
  public void forwardMoveComputer() {
    if (diceRollMovementComputer == 1 || diceRollMovementComputer == 3 || diceRollMovementComputer == 5) {
      positionComputer = positionComputer - 1;
    } else {
      positionComputer = positionComputer - 2;
    }
    if (positionComputer < -9) {
      bombComputer();
    }
    execute();
  }

  //BACKWARD MOVEMENT
  public void backwardMoveComputer() {
    int counterAmmo;
    if (diceRollMovementComputer == 1 || diceRollMovementComputer == 2) {
      positionComputer = positionComputer + 1;
      counterAmmo = 1;
    } else if (diceRollMovementComputer == 3 || diceRollMovementComputer == 4) {
      positionComputer = positionComputer + 2;
      counterAmmo = 2;
    } else {
      positionComputer = positionComputer + 3;
      counterAmmo = 3;
    }
    if (positionComputer > 10) {
      positionComputer = 10;
    }
    ammoComputer = ammoComputer + counterAmmo * NEW_AMMO;
    execute();
  }

  //ATTACK METHODS GROUP
  public void attackMoveComputer() {
    ammoCountComputer();
    attackRollComputer();
    attackCalculationsComputer();
    manCountComputer();
    execute();
  }

  //AMMUNITION COUNT
  public void ammoCountComputer() {
    if (ammoComputer < 1) { // tjekker hvorvidt computeren har mere ammunition
      if (positionComputer <= -7) { //computeren føres frem hvis den allerede er på den sidste halvdel af spillerens banehalvdel
        forwardMoveComputer();
      } else {
        backwardMoveComputer(); //default træk (tilbage)
      }
    }
  }

  public void attackRollComputer() {
    diceRollAttackComputer = random.nextInt(6) + 1;
  }

  //ATTACK DICE ROLLS
  public void attackCalculationsComputer() {
    if (positionPlayer <= positionComputer) {
      int firePower = diceRollAttackComputer * ATTACK_MULTIPLIER;
      if (ammoComputer > firePower) {
        ammoComputer = ammoComputer - firePower;
        int distance = abs(positionPlayer - positionComputer);
        int casualties = MAX_CASUALTIES - distance;
        if (casualties < 0) {
          casualties = 0;
        }
        soldiersPlayer = soldiersPlayer - casualties;
        System.out.printf("\nFjenden har skudt %d af dine soldater!\n", casualties);
      } else {
        computerReaction();
      }
    } else {
      computerReaction();
    }
  }

  //SOLDIER COUNT
  public void manCountComputer() {
    if (soldiersPlayer < 1) {
      System.out.println("\nFjenden har skudt alle dine soldater! Du har desværre tabt spillet!");
      exit(0); // NO MORE PLAYER SOLDIERS - COMPUTER WINS
    }
  }

  //MESSAGE - BOMB - COMPUTER WINS
  public void bombComputer() {
    System.out.println("\nFjenden har sprunget en bombe i din lejer! Du har desværre tabt spillet!");
    exit(0);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //DIVERSE

  //PRINT LINE
  public void printLine() {
    System.out.println("------------------------------------------------------------------------------------------------");
  }

  //BATTLEFIELD MAP
  //visuel slagmark lavet som et array
  public void battlefieldMap() {
    String[] battlefield = new String[21];
    Arrays.fill(battlefield, "_"); // flad terræn
    if (positionPlayer == positionComputer) {
      battlefield[positionPlayer + 10] = "||"; // begge figurer på samme position
    } else {
      battlefield[positionPlayer + 10] = "|-"; //spillerens figur
      battlefield[positionComputer + 10] = "-|"; // computerens figur
    }
    System.out.println(Arrays.toString(battlefield));

    switchTurns();
  }

  //SWITCH TURNS
  public void switchTurns() {
    if ((counterTurns % 2) != 0) {
      playerReaction();
    } else {
      computerReaction();
    }
  }

  //TROUBLESHOOTING
  public void troubleshooting() {
    // DATA - FEJLSØGNING - Valgte at beholde outputs da det var meget rare at have :)
    System.out.printf("\nPlayer position: %d\nDice roll player: %d", positionPlayer, diceRollMovementPlayer);
    //System.out.printf("Computer position: %d\nDice roll computer: %d", positionComputer, diceRollMovementComputer);
    System.out.printf("\nPlayer soldier: %d\nPlayer ammo: %d\n", soldiersPlayer, ammoPlayer);
    //System.out.printf("\nComputer soldiers: %d\nComputer ammo: %d\n", soldiersComputer, ammoComputer);
  }

}
