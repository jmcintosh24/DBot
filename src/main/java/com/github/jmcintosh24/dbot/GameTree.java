package com.github.jmcintosh24.dbot;

/**
 * This class represents a tree, where every node contains information about a particular Steam game. It has various
 * methods for accessing certain items in the tree and for retrieving information about the tree. This particular tree
 * uses red-black ordering to ensure that the tree remains balanced, optimizing search time.
 *
 * @author Jacob McIntosh
 * @version 8/21/2022
 */

public class GameTree {
    private static class Node {
        public Game game;
        public boolean black;
        public Node left;
        public Node right;
    }

    private Node root = null;

    /*
      This method rotates the given node to the left
     */
    private static Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.black = h.black;
        h.black = false;
        return x;
    }

    /*
      This method rotates the given node to the right
     */
    private static Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.black = h.black;
        h.black = false;
        return x;
    }

    /*
      This method flips the color of the given node to red, and its children to black
     */
    private static void flipColors(Node h) {
        h.black = false;
        h.left.black = true;
        h.right.black = true;
    }

    /*
      Checks if the node is red
     */
    private static boolean isRed(Node x) {
        if (x == null)
            return false;
        return !x.black;
    }

    /**
     * Adds the given game into the tree and changes the root to black
     *
     * @param game - the Steam game to be added
     */
    public void add(Game game) {
        root = add(root, game);
        root.black = true;
    }

    /*
      The first code section handles adding a Game to a GameTree. If the tree is empty, the first node is initialized
      with the given game as its game. Otherwise, the new game is added based on the alphabetical value of its name.
      The second section handles the red-black ordering, ensuring that the tree remains balanced.
     */
    private static Node add(Node node, Game game) {
        if (node == null) {
            node = new Node();
            node.game = game;
            node.black = false;
        } else if (node.game.getName().compareTo(game.getName()) > 0)
            node.left = add(node.left, game);
        else {
            node.right = add(node.right, game);
        }

        if (isRed(node.right) && !isRed(node.left))
            node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left))
            node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right))
            flipColors(node);

        return node;
    }

    /**
     * Check if the tree contains the given game
     *
     * @param game - the game that the tree might contain
     * @return whether the tree contains the game
     */
    public boolean contains(Game game) {
        return contains(root, game);
    }

    /*
      Returns true if that game exists in the tree, false otherwise
     */
    private static boolean contains(Node node, Game game) {
        if (node == null)
            return false;
        else if (node.game.getName().compareTo(game.getName()) > 0)
            return contains(node.left, game);
        else if (node.game.getName().compareTo(game.getName()) < 0)
            return contains(node.right, game);
        else
            return true;
    }

    /**
     * Returns the game with the most playtime
     *
     * @return game with the most playtime
     */
    public Game getMostPlayedGame() {
        return getMostPlayedGame(root);
    }

    /*
      Searches for the most played game in the tree.
     */
    private static Game getMostPlayedGame(Node node) {
        if (node == null)
            return null;
        else {
            Game leftGame = getMostPlayedGame(node.left);
            Game rightGame = getMostPlayedGame(node.right);

            if (leftGame != null && rightGame != null) {
                int rightPlaytime = rightGame.getPlaytimeForever();
                int leftPlaytime = leftGame.getPlaytimeForever();
                int playtime = node.game.getPlaytimeForever();

                if (rightPlaytime > leftPlaytime && rightPlaytime > playtime)
                    return rightGame;
                else if (leftPlaytime > rightPlaytime && leftPlaytime > playtime)
                    return leftGame;
            } else if (leftGame == null && rightGame != null) {
                int rightPlaytime = rightGame.getPlaytimeForever();
                int playtime = node.game.getPlaytimeForever();

                if (rightPlaytime > playtime)
                    return rightGame;
            } else if (leftGame != null && rightGame == null) {
                int leftPlaytime = leftGame.getPlaytimeForever();
                int playtime = node.game.getPlaytimeForever();

                if (leftPlaytime > playtime)
                    return leftGame;
            }

            return node.game;
        }
    }

    public Game findGame(String game) {
        return findGame(root, game);
    }

    private Game findGame(Node node, String game) {
        if (node == null)
            return null;
        else if (node.game.getName().compareTo(game) > 0)
            return findGame(node.left, game);
        else if (node.game.getName().compareTo(game) < 0)
            return findGame(node.right, game);
        else
            return node.game;
    }
}
