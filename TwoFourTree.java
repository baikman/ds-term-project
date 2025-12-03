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

    public boolean leftTransferPossible(TFNode curr) {
        if (curr == root()) return false;

        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);

        if (childIdx > parent.getNumItems() - 1) return false;

        // I need to double-check if he expects left transfer ONLY from right sibling or if expected from 'righter' siblings too.
        for (int i = childIdx + 1; i <= parent.getNumItems(); i++) {
            TFNode sibling = parent.getChild(i);
            if (sibling.getNumItems() > 1) return true;
        }

        return false;
    }

    public boolean rightTransferPossible(TFNode curr) {
        if (curr == root()) return false;

        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);

        if (childIdx == 0) return false;

        // same comment from above [left].
        for (int i = childIdx - 1; i >= 0; i--) {
            TFNode sibling = parent.getChild(i);
            if (sibling.getNumItems() > 1) return true;
        }

        return false;
    }

    public boolean leftFusionPossible(TFNode curr) {
        TFNode parent = curr.getParent();
        int childNum = whatChild(curr);
        TFNode leftSib = parent.getChild(childNum - 1);
        int sibNumItems = leftSib.getNumItems();

        return (sibNumItems == 1);
    }

    public void leftTransfer(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode rightSib = parent.getChild(childIdx + 1);
        Item parItem = parent.getItem(0);

        curr.addItem(0, parItem);
        parent.replaceItem(0, rightSib.getItem(0));
        rightSib.removeItem(0);
    }

    public void rightTransfer(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode leftSib = parent.getChild(childIdx - 1);
        Item parItem = parent.getItem(childIdx - 1);

        curr.addItem(0, parItem);
        parent.replaceItem(childIdx - 1, leftSib.getItem(leftSib.getNumItems() - 1));
        leftSib.removeItem(leftSib.getNumItems() - 1);
    }

    public void leftFusion(TFNode curr) { //SOMETHING IS MESSED UP WITH LEFT FUSION AND RIGHT FUSION
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode left = parent.getChild(childIdx - 1);
        Item parItem = parent.getItem(0);
        
        rightSib.insertItem(0, parItem);
        parent.removeItem(0);
        // unclear of what to do here (i wrote rightFusion first so pretend its the left version):
        // leftSib.setChild(leftSib.getNumItems(), curr.getChild(0));
    }

    public void rightFusion(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode rightSib = parent.getChild(childIdx + 1);
        Item parItem = parent.getItem(parent.getNumItems());

        rightSib.insertItem(0, parItem);
        rightSib.setChild(0, curr.getChild(0));
        parent.removeItem(parent.getNumItems());
        // unclear of what to do here:
        // leftSib.setChild(leftSib.getNumItems(), curr.getChild(0));
    }

    public void fixUnderflow(TFNode curr) {
        if (leftTransferPossible(curr)) {
            leftTransfer(curr);
            return;
        }

        else if (rightTransferPossible(curr)) {
            rightTransfer(curr);
            return;
        }

        else if (leftFusionPossible(curr)) {
            leftFusion(curr);
            return;
        }
        
        else {
            rightFusion(curr);
            return;
        }
    }

    public void fixOverflow(TFNode curr) { // WE SHOULD DEFINITELY MAKE SOME SUBROUTINES
        if (curr == root()) {
            TFNode newRoot = new TFNode();
            Item rootItem = curr.getItem(2);

            newRoot.addItem(0, rootItem);
            setRoot(newRoot);
            newRoot.setChild(0, curr);

            curr.removeItem(2);

            TFNode newChild = new TFNode();
            Item childItem = curr.getItem(2);

            curr.setParent(newRoot);
            newChild.setParent(newRoot);
            
            newChild.addItem(0, childItem);
            newRoot.setChild(1, newChild);
            curr.removeItem(2);
            
            newChild.setChild(0, curr.getChild(3));
            TFNode child0 = newChild.getChild(0);
            child0.setParent(newChild);
            newChild.setChild(1, curr.getChild(4));
            TFNode child1 = newChild.getChild(1);
            child1.setParent(newChild);
        } else {
            TFNode parent = curr.getParent();
            int childIdx = whatChild(curr);
            Item item = curr.getItem(2);
            int parentIdx = findFG(item, parent);
            
            parent.insertItem(parentIdx, item);
            curr.removeItem(2);

            TFNode newChild = new TFNode();
            Item childItem = curr.getItem(2);
            
            for (int i = parent.getNumItems(); i > childIdx + 1; i--) parent.setChild(i, parent.getChild(i - 1));

            newChild.addItem(0, childItem);
            parent.setChild(childIdx + 1, newChild);
            curr.removeItem(2);
        }
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

        TFNode currNode = new TFNode();
        Item item = new Item(key, element);
        
        // checks if no root yet. actually i misread the code lol this might be redundant but let's leave it in for 'robustness'...
        if (root() == null) {
            currNode.addItem(0, item);
            setRoot(currNode);
            return;
        }

        // if not root, continue
        currNode = root();
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
        while (currNode.getNumItems() == 4) {
            fixOverflow(currNode);
            currNode = currNode.getParent();
        }
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

        Object toReturn = findElement(key);
        TFNode currNode = root();
        int idx = -1;

        while (currNode != null) {
            idx = findFG(key, currNode);
            Item currItem = currNode.getItem(idx);

            if (currItem.key() == key) {
                if (currNode.getChild(0) == null) currNode.removeItem(idx); // at a leaf

                // else at internal node
                currNode.deleteItem(idx);

                //Make a copy of currNode to iterate through in order to find in order successor
                TFNode downNode = currNode;

                // Finding the in-order successor
                downNode = downNode.getChild(idx + 1);

                // keep going left until you reach last child
                while (downNode.getChild(0) != null) downNode = downNode.getChild(0);

                Item successor = downNode.getItem(0);
                currNode.addItem(idx, successor);
                downNode.removeItem(0);

                while (downNode != currNode && downNode.getNumItems() == 0) {
                    fixUnderflow(downNode);
                    downNode = downNode.getParent();
                }

                while (currNode.getNumItems() == 0) {
                    fixUnderflow(currNode);
                    currNode = currNode.getParent();
                }
                break;
            }

            currNode = currNode.getChild(idx);
        }

        return toReturn;
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
