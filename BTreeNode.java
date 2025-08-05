/**
 * Nó de uma B-Tree especializada para armazenar objetos Book.
 * Ordena e compara livros pelo seu ID.
 */
public class BTreeNode {
    Livro[] chaves;                     // chaves do nó (livros)
    BTreeNode[] filhos;     // filhos
    int n;                           // número de chaves atuais
    boolean folha;                    // indica se é folha
    int t;                           // grau mínimo

    public BTreeNode(int t, boolean folha) {
        this.t = t;
        this.folha = folha;
        this.chaves = new Livro[2 * t - 1];
        this.filhos = new BTreeNode[2 * t];
        this.n = 0;
    }

    public Livro buscar(long id) {
        int i = 0;
        while (i < n && chaves[i].getId() < id) i++;
        if (i < n && chaves[i].getId() == id) return chaves[i];
        if (folha) return null;
        return filhos[i].buscar(id);
    }

    public void insertNaoVazio(Livro livro) {
        int i = n - 1;
        if (folha) {
            while (i >= 0 && chaves[i].compareTo(livro) > 0) {
                chaves[i + 1] = chaves[i];
                i--;
            }
            chaves[i + 1] = livro;
            n++;
        } else {
            while (i >= 0 && chaves[i].compareTo(livro) > 0) i--;
            i++;
            if (filhos[i].n == 2 * t - 1) {
                divideFilhos(i, filhos[i]);
                if (chaves[i].compareTo(livro) < 0) i++;
            }
            filhos[i].insertNaoVazio(livro);
        }
    }

    public void divideFilhos(int idx, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.folha);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) z.chaves[j] = y.chaves[j + t];
        if (!y.folha) {
            for (int j = 0; j < t; j++) z.filhos[j] = y.filhos[j + t];
        }
        y.n = t - 1;
        for (int j = n; j >= idx + 1; j--) filhos[j + 1] = filhos[j];
        filhos[idx + 1] = z;
        for (int j = n - 1; j >= idx; j--) chaves[j + 1] = chaves[j];
        chaves[idx] = y.chaves[t - 1];
        n++;
    }

    public void remove(long id) {
        int idx = 0;
        while (idx < n && chaves[idx].getId() < id) idx++;
        if (idx < n && chaves[idx].getId() == id) {
            if (folha) removeFolha(idx);
            else removeInterior(idx);
        } else {
            if (folha) return;
            boolean lastChild = (idx == n);
            if (filhos[idx].n < t) preencher(idx);
            if (lastChild && idx > n) filhos[idx - 1].remove(id);
            else filhos[idx].remove(id);
        }
    }

    private void removeFolha(int idx) {
        for (int i = idx + 1; i < n; i++) chaves[i - 1] = chaves[i];
        n--;
    }

    private void removeInterior(int idx) {
        Livro livro = chaves[idx];
        if (filhos[idx].n >= t) {
            Livro pred = getAnterior(idx);
            chaves[idx] = pred;
            filhos[idx].remove(pred.getId());
        } else if (filhos[idx + 1].n >= t) {
            Livro posterior = getPosterior(idx);
            chaves[idx] = posterior;
            filhos[idx + 1].remove(posterior.getId());
        } else {
            juntar(idx);
            filhos[idx].remove(livro.getId());
        }
    }

    private Livro getAnterior(int idx) {
        BTreeNode atual = filhos[idx];
        while (!atual.folha) atual = atual.filhos[atual.n];
        return atual.chaves[atual.n - 1];
    }

    private Livro getPosterior(int idx) {
        BTreeNode atual = filhos[idx + 1];
        while (!atual.folha) atual = atual.filhos[0];
        return atual.chaves[0];
    }

    private void preencher(int idx) {
        if (idx != 0 && filhos[idx - 1].n >= t) emprestaAnterior(idx);
        else if (idx != n && filhos[idx + 1].n >= t) emprestaProximo(idx);
        else {
            if (idx != n) juntar(idx);
            else juntar(idx - 1);
        }
    }

    private void emprestaAnterior(int idx) {
        BTreeNode filho = filhos[idx];
        BTreeNode vizinho = filhos[idx - 1];
        for (int i = filho.n - 1; i >= 0; i--) filho.chaves[i + 1] = filho.chaves[i];
        if (!filho.folha) {
            for (int i = filho.n; i >= 0; i--) filho.filhos[i + 1] = filho.filhos[i];
        }
        filho.chaves[0] = chaves[idx - 1];
        if (!filho.folha) filho.filhos[0] = vizinho.filhos[vizinho.n];
        chaves[idx - 1] = vizinho.chaves[vizinho.n - 1];
        vizinho.n--;
        filho.n++;
    }

    private void emprestaProximo(int idx) {
        BTreeNode filho = filhos[idx];
        BTreeNode vizinho = filhos[idx + 1];
        filho.chaves[filho.n] = chaves[idx];
        if (!filho.folha) filho.filhos[filho.n + 1] = vizinho.filhos[0];
        chaves[idx] = vizinho.chaves[0];
        for (int i = 1; i < vizinho.n; i++) vizinho.chaves[i - 1] = vizinho.chaves[i];
        if (!vizinho.folha) {
            for (int i = 1; i <= vizinho.n; i++) vizinho.filhos[i - 1] = vizinho.filhos[i];
        }
        vizinho.n--;
        filho.n++;
    }

    private void juntar(int idx) {
        BTreeNode filho = filhos[idx];
        BTreeNode irmao = filhos[idx + 1];
        filho.chaves[t - 1] = chaves[idx];
        for (int i = 0; i < irmao.n; i++) filho.chaves[i + t] = irmao.chaves[i];
        if (!filho.folha) {
            for (int i = 0; i <= irmao.n; i++) filho.filhos[i + t] = irmao.filhos[i];
        }
        for (int i = idx + 1; i < n; i++) chaves[i - 1] = chaves[i];
        for (int i = idx + 2; i <= n; i++) filhos[i - 1] = filhos[i];
        filho.n += irmao.n + 1;
        n--;
    }

    public void printInOrder() {
        int i;
        for (i = 0; i < n; i++) {
            if (!folha) filhos[i].printInOrder();
            System.out.println(chaves[i]);
        }
        if (!folha) filhos[i].printInOrder();
    }
}
