import java.util.Random;

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
    
    /**
     * @param key to check validity of
     * @exception thrown if key not comparable
     */
    public void checkValidKey(Object key) {
        if (treeComp.isComparable(key)) return;

        throw new InvalidIntegerException("Key is not comparable");
    }

    /**
     * @param child to be searched for
     * @return int corresponding with the child
     */
    public int whatChild(TFNode child) {
        if (child == root()) throw new TwoFourTreeException("Cannot return child index of root");
        TFNode parent = child.getParent();
        for (int i = 0; i <= parent.getNumItems(); i++) {
            if (parent.getChild(i) == child) return i;
        }
        throw new TwoFourTreeException("Could not return child index");
    }

    /**
     * @param node to check if left transfer possible from
     * @return boolean correlating to left transfer being possible
     */
    public boolean leftTransferPossible(TFNode curr) {
        if (curr == root()) return false;

        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);

        if (childIdx > parent.getNumItems() - 1) return false;

        TFNode sibling = parent.getChild(childIdx + 1);
        return (sibling.getNumItems() > 1);
    }
    
    /**
     * @param node to check if right transfer possible from
     * @return boolean correlating to right transfer being possible
     */
    public boolean rightTransferPossible(TFNode curr) {
        if (curr == root()) return false;

        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);

        if (childIdx == 0) return false;

        TFNode sibling = parent.getChild(childIdx - 1);
        return (sibling.getNumItems() > 1);
    }

    /**
     * @param node to check if left fusion possible from
     * @return boolean correlating to left fusion being possible
     */
    public boolean leftFusionPossible(TFNode curr) {
        TFNode parent = curr.getParent();
        int childNum = whatChild(curr);

        if (childNum == 0) return false;

        TFNode leftSib = parent.getChild(childNum - 1);
        int sibNumItems = leftSib.getNumItems();

        return (sibNumItems == 1);
    }

    /**
     * @param node to perform left transfer on
     */
    public void leftTransfer(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode rightSib = parent.getChild(childIdx + 1);
        Item parItem = parent.getItem(0);

        curr.addItem(0, parItem);
        parent.replaceItem(0, rightSib.getItem(0));

        TFNode sibChild = rightSib.getChild(0);

        if (sibChild != null) {
            curr.setChild(1, sibChild);
            sibChild.setParent(curr);
        }

        rightSib.removeItem(0);
    }

    /**
     * @param node to perform right transfer on
     */
    public void rightTransfer(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        TFNode leftSib = parent.getChild(childIdx - 1);
        Item parItem = parent.getItem(childIdx - 1);

        curr.addItem(0, parItem);
        parent.replaceItem(childIdx - 1, leftSib.getItem(leftSib.getNumItems() - 1));

        TFNode sibChild = leftSib.getChild(leftSib.getNumItems());

        if (sibChild != null) {
            curr.setChild(1, curr.getChild(0));
            curr.setChild(0, sibChild);
            sibChild.setParent(curr);
        }
        
        leftSib.removeItem(leftSib.getNumItems() - 1);
    }

    /**
     * @param node to perform left fusion on
     */
    public void leftFusion(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        int parentIdx = childIdx - 1;
        TFNode leftSib = parent.getChild(parentIdx);
        Item parItem = parent.getItem(parentIdx);

        leftSib.addItem(leftSib.getNumItems(), parItem);

        TFNode child = curr.getChild(0);
        
        if (child != null) {
            leftSib.setChild(2, child);
            child.setParent(leftSib);
        }

        parent.removeItem(parentIdx);
        parent.setChild(parentIdx, leftSib);
        leftSib.setParent(parent);
    }

    /**
     * @param node to perform right fuison on
     */
    public void rightFusion(TFNode curr) {
        TFNode parent = curr.getParent();
        int childIdx = whatChild(curr);
        int parentIdx = childIdx + 1;
        TFNode rightSib = parent.getChild(parentIdx);
        Item parItem = parent.getItem(childIdx);
        
        rightSib.insertItem(0, parItem);

        TFNode child = curr.getChild(0);
        
        if (child != null) {
            rightSib.setChild(0, child);
            child.setParent(rightSib);
        }

        parent.removeItem(childIdx);
    }

    /**
     * @param node to fix underflow on
     */
    public void fixUnderflow(TFNode curr) {
        if (curr == root()) {
            if (curr.getChild(0) == null) {
               setRoot(curr.getChild(1));
               curr.getChild(1).setParent(null);
               return;
            }
            setRoot(curr.getChild(0));
            curr.getChild(0).setParent(null);
            return;
        }
        /*if (curr.getNumItems() == 0 && curr.getChild(0) != null) {
            if (curr.getChild(0).getNumItems() == 0) {
                curr = curr.getChild(1);
            }
            else if (curr.getChild(1).getNumItems() == 0) {
                curr = curr.getChild(0);
            }
        }*/

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
            if (curr.getParent().getNumItems() == 0) {
                fixUnderflow(curr.getParent());
            }
            return;
        }
        
        else {
            rightFusion(curr);
            if (curr.getParent().getNumItems() == 0) {
                fixUnderflow(curr.getParent());
            }
            return;
        }
    }

    /**
     * @param node to fix overflow on
     */
    public void fixOverflow(TFNode curr) {
        if (curr == root()) {
            TFNode newRoot = new TFNode();
            Item rootItem = curr.getItem(2);

            newRoot.addItem(0, rootItem);
            setRoot(newRoot);
            newRoot.setChild(0, curr);
            curr.setParent(newRoot);

            TFNode newChild = new TFNode();
            
            newRoot.setChild(1, newChild);
            newChild.setParent(newRoot);

            Item childItem = curr.getItem(3);

            newChild.addItem(0, childItem);
            
            TFNode child0 = curr.getChild(3);
            newChild.setChild(0, child0);

            TFNode child1 = curr.getChild(4);
            newChild.setChild(1, child1);
            
            if (child0 != null) child0.setParent(newChild);
            if (child1 != null) child1.setParent(newChild);

            curr.deleteItem(3);
            curr.deleteItem(2);
        }
        else {
            TFNode parent = curr.getParent();
            int childIdx = whatChild(curr);
            Item item = curr.getItem(2);
            
            parent.insertItem(childIdx, item);

            TFNode newChild = new TFNode();
            Item childItem = curr.getItem(3);

            newChild.addItem(0, childItem);
            parent.setChild(childIdx + 1, newChild);
            newChild.setParent(parent);

            TFNode child0 = curr.getChild(3);
            newChild.setChild(0, child0);

            TFNode child1 = curr.getChild(4);
            newChild.setChild(1, child1);
            
            if (child0 != null) child0.setParent(newChild);
            if (child1 != null) child1.setParent(newChild);
            
            curr.deleteItem(3);
            curr.deleteItem(2);
        }
    }

    /**
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
        
        TFNode currNode = root();
        int idx = -1;

        while (currNode != null) {
            idx = findFG(key, currNode);
            if (currNode.getNumItems() > idx) {
                Item currItem = currNode.getItem(idx);
                if (treeComp.isEqual(currItem.key(), key)) return currItem.element();
            }
 
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
        
        if (root() == null) {
            currNode.addItem(0, item);
            setRoot(currNode);
            return;
        }

        currNode = root();
        int idx = findFG(key, currNode);

        while (currNode.getChild(idx) != null) {
            currNode = currNode.getChild(idx);
            
            idx = findFG(key, currNode);

            if (currNode.getNumItems() < idx) {
                Item currItem = currNode.getItem(idx);

                // Handling duplicates
                if (treeComp.isEqual(currItem.key(), key)) {
                    if (currNode.getChild(idx) != null) {
                        // Finding the in-order successor
                        currNode = currNode.getChild(idx + 1);

                        // Keep going left until you reach last child
                        while (currNode.getChild(0) != null) currNode = currNode.getChild(0);

                        break;
                    }
                }
            }
        }

        currNode.insertItem(idx, item);

        // Check for overflow
        while (currNode.getNumItems() == 4) {
            fixOverflow(currNode);
            currNode = currNode.getParent();
        }

        size++;
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

        if (findElement(key) == null) throw new ElementNotFoundException("Element not found");

        Object toReturn = findElement(key);
        TFNode currNode = root();
        int idx = -1;

        while (currNode != null) {
            idx = findFG(key, currNode);
            if (idx < currNode.getNumItems()) {
                Item currItem = currNode.getItem(idx);

                if (treeComp.isEqual(currItem.key(), key)) {
                    if (currNode.getChild(0) == null) {
                        currNode.removeItem(idx);
                        if (currNode.getNumItems() == 0) {
                            if (currNode == root() && currNode.getChild(0) == null) {
                                return toReturn;
                            }
                            else if (currNode == root()) {
                                fixUnderflow(currNode);
                                break;
                            }
                            fixUnderflow(currNode);
                            if (currNode.getParent() != null && currNode != root()) currNode = currNode.getParent();
                        }
                    }
                    else  {
                        TFNode downNode = currNode;

                        // Find the in-order successor
                        downNode = downNode.getChild(idx + 1);

                        // Keep going left until you reach last child
                        while (downNode.getChild(0) != null) downNode = downNode.getChild(0);

                        Item successor = downNode.getItem(0);
                        currNode.replaceItem(idx, successor);
                        downNode.removeItem(0);

                        if (downNode.getNumItems() == 0) {
                            fixUnderflow(downNode);
                        }
                    }

                    break;
                }
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

        myTree.checkTree();

        myTree = new TwoFourTree(myComp);

        int TEST_SIZE = 20;
        int nums[] = new int[TEST_SIZE];
        Random rand = new Random(2);

        System.out.println("Adding " + TEST_SIZE);
        
        for (int i = 0; i < TEST_SIZE; i++) {
            int num = rand.nextInt(TEST_SIZE);
            nums[i] = num;
            
            myTree.insertElement(num, num);
            myTree.checkTree();
        }

        myTree.printAllElements();

        System.out.println("Removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            System.out.println("Removing " + nums[i]);
            int out = (Integer) myTree.removeElement(nums[i]);
            System.out.println("Removed " + out);
            myTree.checkTree();
            myTree.printAllElements();
            System.out.println();

            if (out != nums[i]) throw new TwoFourTreeException("main: wrong element removed");
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
