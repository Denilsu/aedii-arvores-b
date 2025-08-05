public class Livro implements Comparable<Livro> {
    private long id;
    private String titulo;
    private String autor;

    public Livro(long id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }

    @Override
    public int compareTo(Livro other) {
        return Long.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return id + " - " + titulo + " by " + autor;
    }
}