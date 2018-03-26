package com.maoyu.tree.avltree;

/**
 * @author maoyu [2018-03-23 14:25]
 **/
public class AVLTree<T extends Comparable> {
    private AVLTree<T> left;//左子节点
    private AVLTree<T> right;//右子节点
    private int height;//节点的高度
    private T data;//存入的数据

    private AVLTree<T> root;//根节点

    public AVLTree(AVLTree<T> left, AVLTree<T> right, int height, T data) {
        this.left = left;
        this.right = right;
        this.height = height;
        this.data = data;
    }

    public AVLTree(AVLTree<T> left, AVLTree<T> right, T data) {
        this(left, right, 0, data);
    }

    public AVLTree(T data) {
        this(null, null, data);
    }

    public AVLTree() {
        root = null;
    }

    /**
     * 计算高度，空节点为-1
     *
     * @return
     */
    public int height() {
        return this.height(root);
    }

    private int height(AVLTree<T> tree) {
        return tree == null ? -1 : tree.height;
    }


    /**
     * LL-左左单旋转（向右旋转）
     *
     * @param tree
     * @return
     */
    private AVLTree<T> leftRotate(AVLTree<T> tree) {
        AVLTree<T> leftChild = tree.left;//获取tree的左子节点
        tree.left = leftChild.right;//将tree的左子节点设为leftChild的右子节点
        leftChild.right = tree; //将tree变为leftChild的右子节点

        //计算高度
        tree.height = Math.max(height(tree.left), height(tree.right)) + 1;
        leftChild.height = Math.max(height(leftChild.left), tree.height) + 1;
        return leftChild;
    }

    /**
     * RR-右右单旋转（向左旋转）
     *
     * @param tree
     * @return
     */
    private AVLTree<T> rightRotate(AVLTree tree) {
        AVLTree<T> rightChild = tree.right;//获取tree的右子节点
        tree.right = rightChild.left;//将tree的右子节点设为leftChild的左子节点
        rightChild.left = tree;//将tree变为leftChild的左子节点

        //计算高度
        tree.height = Math.max(height(tree.left), height(tree.right)) + 1;
        rightChild.height = Math.max(tree.height, height(rightChild.right)) + 1;
        return rightChild;
    }

    /**
     * LR-左右双旋转（先向左旋转，再向右旋转）
     *
     * @param tree
     * @return
     */
    private AVLTree<T> leftRightRotate(AVLTree tree) {
        tree.left = this.rightRotate(tree.left);//先RR
        return this.leftRotate(tree);//后LL
    }

    /**
     * RL-右左旋转（先向右旋转，再向左旋转）
     *
     * @param tree
     * @return
     */
    private AVLTree<T> rightLeftRotate(AVLTree tree) {
        tree.right = this.leftRotate(tree.right);//先LL
        return this.rightRotate(tree);//后RR
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void insertTree(T data) {
        if (data == null) {
            throw new RuntimeException("没有数据可添加");
        }
        this.root = this.insertTree(data, root);

    }

    private AVLTree<T> insertTree(T data, AVLTree root) {
        if (root == null) {
            root = new AVLTree<T>(data);
        }
        //比根小，插入到左子树
        else if (data.compareTo(root.data) < 0) {
            //递归找到插入位置
            root.left = this.insertTree(data, root.left);

            //计算子树高度，等于2需重新恢复平衡
            if (this.height(root.left) - this.height(root.right) == 2) {
                //判断data是插入点的左孩子还是右孩子
                if (data.compareTo(root.left.data) < 0) {
                    //LL旋转
                    root = this.leftRotate(root);
                } else {
                    //否则进行左右旋转
                    root = this.leftRightRotate(root);
                }
            }
        }
        //比根大，插入右子树
        else if (data.compareTo(root.data) > 0) {
            root.right = this.insertTree(data, root.right);
            if (this.height(root.right) - this.height(root.left) == 2) {
                if (data.compareTo(root.right.data) > 0) {
                    //RR右旋
                    root = this.rightRotate(root);
                } else {
                    //否则进行右左旋转
                    root = this.rightLeftRotate(root);
                }
            }
        } else {
            ;
        }
        //重新计算节点高度
        root.height = Math.max(height(root.left), height(root.right)) + 1;
        return root;
    }

    /**
     * 删除数据
     *
     * @param data
     * @throws Exception
     */
    public void remove(T data) throws Exception {
        if (data == null) {
            throw new Exception("删除的数据不能为空");
        }
        this.root = remove(data, root);
    }

    private AVLTree<T> remove(T data, AVLTree<T> root) {
        if (root == null) {
            return null;
        }
        //从左子树中查找
        if (data.compareTo(root.data) < 0) {
            root.left = this.remove(data, root.left);

            //检测是否不平衡
            if (this.height(root.right) - this.height(root.left) == 2) {
                AVLTree<T> avlTree = root.right;
                //判断是RR还是RL
                if (height(avlTree.right) > height(avlTree.left)) {
                    //RR
                    root = this.rightRotate(root);
                } else {
                    //RL
                    root = this.rightLeftRotate(root);
                }
            }
        }
        //从右子树中查找
        else if (data.compareTo(root.data) > 0) {
            root.right = this.remove(data, root.right);

            if (height(root.left) - height(root.right) == 2) {
                AVLTree<T> avlTree = root.left;
                //判断是LL还是LR
                if (height(avlTree.left) > height(avlTree.right)) {
                    //LL
                    root = this.leftRotate(root);
                } else {
                    //LR
                    root = this.leftRightRotate(root);
                }
            }
        }
        //已找到删除的元素且该节点有两个子节点
        else if (root.right != null && root.left != null) {
            //寻找替代的节点(该节点的右子树的最小节点，也可以选择左子树的最大节点)
            root.data = this.getMin(root.right).data;
            //移除替换的节点
            root.right = remove(root.data, root.right);
        }
        //只有一个或没有节点的情况
        else {
            root = (root.left == null) ? root.right : root.left;
        }
        if (root != null) {
            //更新高度值
            root.height = Math.max(height(root.left), height(root.right)) + 1;
        }
        return root;
    }

    /**
     * 查找最小值
     *
     * @return
     */
    public T getMin() {
        return this.getMin(root).data;
    }

    private AVLTree<T> getMin(AVLTree<T> tree) {
        if (tree == null) {
            return null;
        } else if (tree.left == null) {
            //如果tree没有left，则最小
            return tree;
        } else {
            //递归查找左子树
            return this.getMin(tree.left);
        }

    }

    /**
     * 查找最大值
     *
     * @return
     */
    public T getMax() {
        return this.getMax(root).data;
    }

    private AVLTree<T> getMax(AVLTree<T> tree) {
        if (tree == null) {
            return null;
        } else if (tree.right == null) {
            return tree;
        } else {
            return this.getMax(tree.right);
        }
    }

    /**
     * 是否包含该元素
     *
     * @param data
     * @return
     */
    public boolean isContains(T data) {
        return data != null && this.contain(data, root);
    }

    private boolean contain(T data, AVLTree<T> tree) {
        if (tree == null) {
            return false;
        }
        if (data.compareTo(tree.data) < 0) {
            return this.contain(data, tree.left);
        } else if (data.compareTo(tree.data) > 0) {
            return this.contain(data, tree.right);
        } else {
            return true;
        }
    }

    /**
     * 先序遍历
     *
     * @return
     */
    public String preOrderTraversal() {
        String sb = this.preOrderTraversal(root);
        if (sb.length() > 0) {
            //去掉最后的逗号
            sb = sb.substring(0, sb.length() - 1);
        }
        return sb;
    }

    private String preOrderTraversal(AVLTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        if (tree != null) {
            //先访问根节点
            sb.append(tree.data).append(",");
            //再访问左子树
            sb.append(this.preOrderTraversal(tree.left));
            //再访问右子树
            sb.append(this.preOrderTraversal(tree.right));
        }
        return sb.toString();
    }

    /**
     * 中序遍历
     *
     * @return
     */
    public String inOrderTraversal() {
        String sb = this.inOrderTraversal(root);
        if (sb.length() > 0) {
            //去掉最后的逗号
            sb = sb.substring(0, sb.length() - 1);
        }
        return sb;
    }

    private String inOrderTraversal(AVLTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        if (tree != null) {
            //先访问左子树
            sb.append(this.inOrderTraversal(tree.left));
            //再访问根节点
            sb.append(tree.data).append(",");
            //再访问右子树
            sb.append(this.inOrderTraversal(tree.right));
        }
        return sb.toString();
    }

    /**
     * 后序遍历
     *
     * @return
     */
    public String postOrderTraversal() {
        String sb = this.postOrderTraversal(root);
        if (sb.length() > 0) {
            //去掉最后的逗号
            sb = sb.substring(0, sb.length() - 1);
        }
        return sb;
    }

    private String postOrderTraversal(AVLTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        if (tree != null) {
            //先访问左子树
            sb.append(this.postOrderTraversal(tree.left));
            //再访问右子树
            sb.append(this.postOrderTraversal(tree.right));
            //再访问根节点
            sb.append(tree.data).append(",");
        }
        return sb.toString();
    }

    /**
     * 打印Tree
     *
     * @param root
     */
    private void printTree(AVLTree<T> root) {
        if (root != null) {
            printTree(root.left);
            System.out.print(root.data);
            printTree(root.right);
        }
    }


    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int[] ints = {2, 1, 0, 3, 4, 5, 6, 9, 8, 7};
        AVLTree avlTree = new AVLTree();
        for (int i = 0; i < ints.length; i++) {
            avlTree.insertTree(ints[i]);
            System.out.println("插入数据" + ints[i]);
            System.out.println(avlTree.preOrderTraversal());//先序遍历
//            System.out.println(avlTree.inOrderTraversal());//中序遍历
//            System.out.println(avlTree.postOrderTraversal());//后序遍历
        }
        avlTree.remove(6);
        System.out.println(avlTree.preOrderTraversal());
        System.out.println(avlTree.isContains(6));
        System.out.println(avlTree.getMin());
        System.out.println(avlTree.getMax());
        avlTree.printTree(avlTree.root);
    }


}
