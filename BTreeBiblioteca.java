import java.util.Timer;

/**
 * B-Tree especializada para armazenar livros com ID, título e autor.
 */
public class BTreeBiblioteca {
    private BTreeNode raiz;
    private int t;

    public BTreeBiblioteca(int t) {
        this.t = t;
        this.raiz = null;
    }

    public Livro search(long id) {
        return (raiz == null) ? null : raiz.buscar(id);
    }

    public void insert(Livro livro) {
        if (raiz == null) {
            raiz = new BTreeNode(t, true);
            raiz.chaves[0] = livro;
            raiz.n = 1;
        } else {
            if (raiz.n == 2 * t - 1) {
                BTreeNode s = new BTreeNode(t, false);
                s.filhos[0] = raiz;
                s.divideFilhos(0, raiz);
                int idx = s.chaves[0].compareTo(livro) < 0 ? 1 : 0;
                s.filhos[idx].insertNaoVazio(livro);
                raiz = s;
            } else {
                raiz.insertNaoVazio(livro);
            }
        }
    }

    public void remove(long id) {
        if (raiz == null) return;
        raiz.remove(id);
        if (raiz.n == 0) {
            raiz = raiz.folha ? null : raiz.filhos[0];
        }
    }

    //Chama a funcao recursiva para simplificar na Main
    public void printTreeByNodes(){
        printNodeRecursive(raiz,0);
    }
    //Exibe a arvore de acordo com cada No/Pagina de chaves
    private void printNodeRecursive(BTreeNode node, int h){
        if(node == null){
            return;
        }
        System.out.println("h = " + h);

        //imprime os livros do atual nó
        for(int i = 0; i<node.n; i++){
            Livro livro = node.chaves[i];
            System.out.printf("[%d]", livro.getId());
        }
        if (!node.folha) {
            for (int i = 0; i <= node.n; i++) {
                System.out.println();
                System.out.println("========================"); // linha entre nós
                // Chamada recursiva para os filhos
                printNodeRecursive(node.filhos[i], h+1);
            }
        }
    }

    public static void main(String[] args) {
        long idAlvo = 50000L;
        Livro livro;
        // Validação de parâmetros
        if (args.length != 1) {
            System.err.println("Uso: java Main <arquivo_de_livros.txt>");
            System.exit(1);
        }
        String filePath = args[0];

        //Cria a árvore B que vai armazenar os livros
        BTreeBiblioteca tree = new BTreeBiblioteca(10); // grau mínimo t = 10
        LoaderBiblioteca.inserirLivros(tree, filePath);//Carrega todos os livros do .txt para a árvore

        //Exemplo de busca para verificar que os livros foram inseridos
        long startTime = System.nanoTime();
        livro = tree.search(idAlvo);
        long endTime = System.nanoTime();
        long durationTotal = (endTime - startTime);
        System.out.printf("\nTempo de busca: %d ns%n", durationTotal);

        if (livro != null) {
            System.out.println("Encontrado: " + livro);
        } else {
            System.out.println("Livro " + livro + " nao encontrado.");
        }
        //exibe a arvore pelos nos
        //tree.printTreeByNodes();

        //Remover Livros
        LoaderBiblioteca.removerLivros(tree, filePath);
        tree.printTreeByNodes();
    }
}
