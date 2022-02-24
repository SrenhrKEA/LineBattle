package demo;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Test {
  int playerAmmo = 0;
  Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    //new Test().playerReaction();
    int positionPlayer = 5;
    int positionComputer = 4;

    System.out.println(positionPlayer - positionComputer);

  }

  public void playerReaction() {
    System.out.println("Bestem hvad dine soldater skal gøre:");
    System.out.print("\n1. Angrib\n2. Fremad\n3. Tilbage\nHvad vælger du?: ");
    String reaction = in.nextLine();
    switch (reaction) {
      case "1", "angrib", "Angrib" -> attackMovePlayer();
      //case "2","fremad","Fremad" -> forwardMovePlayer();
      //case "3","tilbage","Tilbage" -> backwardMovePlayer();
      default -> {
        System.out.println("Det virkede ikke. Prøv igen");
        playerReaction();
      }
    }
  }

  // Attack Method Group
  public void attackMovePlayer() {
    ammoCountPlayer();
    attackRollPlayer();
    manCountPlayer();
  }

  // Counting Ammunition
  public void ammoCountPlayer() {
    if (playerAmmo < 1) {
      System.out.println("Dine soldater er løbet tør for ammunition og kan ikke angribe!");
      playerReaction();
    }
  }

  // Attack Rolls
  public void attackRollPlayer() {
    System.out.println("Attack Roll");
  }

  //Counting Soldiers
  public void manCountPlayer() {
    System.out.println("Man Count");
  }
}

