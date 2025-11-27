/**
* (2,4) Tree
*
* @author Abby Wurster, Brandon Aikman
* @version 1.0
* File: TwoFourTree.java
* Created: Nov 2025
* Summary of Modifications: Second version
* ©Copyright Cedarville University, its Computer Science faculty, and the author.
*
* Description: (2,4) Tree implementation.
*/

public class TwoFourTree implements Dictionary {

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }
    
    // DON'T PANIC WE'RE SUPPOSED TO HAVE A LOT OF SUBROUTINES FOR READABILITY AND WHATNOT
    // ...
    public void checkValidKey(Object key) {
        if (treeComp.isComparable(key)) return;

        throw new InvalidIntegerException("Key is not comparable");
    }

    /**
     * @param child to be searched for
     * @return int corresponding with the child
     */
    public int whatChild(TFNode child) {
        TFNode parent = child.getParent();
        for (int i = 0; i < parent.getNumItems(); i++) {
            if (parent.getChild(i) == child) return i;
        }
        return parent.getNumItems();
    }

    public boolean canDoLeftTransfer(TFNode curr) { // I FEEL LIKE THIS MIGHT BE TOO VERBOSE BUT WE CAN FIX IT LATER
        int childNum = whatChild(curr);
        if (childNum == 0) return false;

        TFNode parent = curr.getParent();
        TFNode leftSib = parent.getChild(childNum - 1);

        if (leftSib.getNumItems() <= 1) return false;

        return true;
    }

    public boolean canDoRightTransfer(TFNode temp) {
        int childNum = whatChild(temp);
        TFNode parent = temp.getParent();

        if (parent.getNumItems() == childNum) return false;

        TFNode rightSib = parent.getChild(childNum + 1);
        if (rightSib.getNumItems() <= 1) return false;

        return true;
    }

    public boolean canDoLeftFusion(TFNode temp) {
        int childNum = whatChild(temp);
        if (childNum == 0) return false;

        return true;
    }

    public void leftTransfer() {
        //TODO: implement
    }

    public void rightTransfer() {
        //TODO: implement
    }

    public void leftFusion() {
        //TODO: implement
    }

    public void rightFusion() {
        //TODO: implement
    }

    // PLEASE DON'T CHANGE ANYTHING UNTIL WE CAN TALK ABOUT IT
    // OR AT LEAST JUST COMMENT OUT MY CODE IF YOU DO CHANGE ANYTHING
    // okay but why did you change things you weren't supposed to? without communicating? I had/have changes I haven't pushed yet...
    public void fixUnderflow(TFNode curr) {
        if (canDoLeftTransfer(curr)) {
            leftTransfer();
            return;
        }

        else if (canDoRightTransfer(curr)) {
            rightTransfer();
            return;
        }

        else if (canDoLeftFusion(curr)) {
            leftFusion();
            return;
        }
        else {
            rightFusion();
            return;
        }
    }

    public void fixOverflow(TFNode curr) {
        //TODO: implement
    }

    /**
     * 
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    public int findFG(Object key, TFNode curr) {
        Item currItem = new Item();
        for (int i = 0; i < curr.getNumItems(); i++) {
            currItem = curr.getItem(i);
            if (treeComp.isGreaterThanOrEqualTo(currItem.element(), key)) return i;
        }

        return curr.getNumItems();
    }

    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    public Object findElement(Object key) {
        checkValidKey(key);
        
        TFNode currNode = treeRoot;
        int idx = -1;

        while (currNode != null) {
            idx = findFG(key, currNode);
            Item currItem = currNode.getItem(idx);

            if (currItem.key() == key) return currItem.element();

            currNode = currNode.getChild(idx);
        }

        return null;
    }

    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    public void insertElement(Object key, Object element) {
        checkValidKey(key);

        TFNode currNode = treeRoot;
        Item item = new Item(key, element);
        int idx = findFG(key, currNode);

        while (currNode.getChild(idx) != null) {
            currNode = currNode.getChild(idx);
            
            idx = findFG(key, currNode);
            Item currItem = currNode.getItem(idx);

            // Handling duplicates
            if (currItem.key() == key) {
                if (currNode.getChild(idx) != null) {
                    // Finding the in-order successor
                    currNode = currNode.getChild(idx + 1);

                    // keep going left until you reach last child
                    while (currNode.getChild(0) != null) currNode = currNode.getChild(0);

                    break;
                }
            }
        }

        currNode.insertItem(idx, item);

        // Check for overflow
        if (currNode.getNumItems() == 4) fixOverflow(currNode);
    }

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    public Object removeElement(Object key) throws ElementNotFoundException {
        checkValidKey(key);

        if (findElement(key) == null) throw new ElementNotFoundException();

        TFNode currNode = treeRoot;
        int idx = -1;

        while (currNode != null) {
            idx = findFG(key, currNode);
            Item currItem = currNode.getItem(idx);

            if (currItem.key() == key) {
                currNode.removeItem(idx);
                if (currNode.getNumItems() == 0) {
                    fixUnderflow(currNode);
                }
                break;
            }

            currNode = currNode.getChild(idx);
        }

        return findElement(key);
    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        myTree.insertElement(myInt1, myInt1);
        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);

        Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);

        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);

        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);

        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);

        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);

        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);

        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);

        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);

        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);

        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);

        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);

        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);

        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);

        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);

        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);

        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);

        myTree.printAllElements();
        System.out.println("done");

        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
            //          myTree.printAllElements();
            //         myTree.checkTree();
        }
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) myTree.removeElement(new Integer(i));
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                myTree.printAllElements();
            }
        }
        System.out.println("done");
    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }
}
