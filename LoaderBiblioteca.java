import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoaderBiblioteca {
    long startTime;
    /**
     * Lê o arquivo de texto onde cada livro ocupa 3 linhas:
     *   1) id (long)
     *   2) título
     *   3) autor
     * Sentinel de fim: id == 0 ou EOF.
     */
    public static void inserirLivros(BTreeBiblioteca tree, String filePath) {
        double startTime = System.nanoTime();
        int count = 0;
        try (Scanner scanner = new Scanner(new File(filePath), "UTF-8")) {
            while (scanner.hasNextLine()) {
                String idLine = scanner.nextLine().trim();
                long id;

                // tenta converter ID
                try {
                    id = Long.parseLong(idLine);
                } catch (NumberFormatException e) {
                    System.err.printf("ID inválido (‘%s’), pulando este registro.%n", idLine);
                    continue;
                }

                // sentinel de término
                if (id == 0L) {
                    break;
                }

                // lê título (verifica antes)
                if (!scanner.hasNextLine()) {
                    System.err.printf("Título ausente para ID %d. Encerrando leitura.%n", id);
                    break;
                }
                String title = scanner.nextLine().trim();

                // lê autor (verifica antes)
                if (!scanner.hasNextLine()) {
                    System.err.printf("Autor ausente para ID %d. Encerrando leitura.%n", id);
                    break;
                }
                String author = scanner.nextLine().trim();

                // insere na B-Tree
                Livro livro = new Livro(id, title, author);
                tree.insert(livro);
                count++;
                
            }

        } catch (FileNotFoundException e) {
            System.err.printf("Arquivo nao encontrado: %s%n", filePath);
            return;
        }

        double endTime = System.nanoTime();
        double durationTotal = (endTime - startTime)/1_000_000;
        double durationMedia = (durationTotal/count);
        System.out.printf("Tempo de insercao Total: %f ms\nTempo de insercao MEDIO: %f ms%n", durationTotal, durationMedia);
    }
    public static void removerLivros(BTreeBiblioteca tree, String filePath) {
        double startTime = System.nanoTime();
        int count = 0;
        try (Scanner scanner = new Scanner(new File(filePath), "UTF-8")) {
            while (scanner.hasNextLine()) {
                String idLine = scanner.nextLine().trim();
                long id;

                // tenta converter ID
                try {
                    id = Long.parseLong(idLine);
                } catch (NumberFormatException e) {
                    System.err.printf("ID inválido (‘%s’), pulando este registro.%n", idLine);
                    continue;
                }

                // sentinel de término
                if (id == 0L) {
                    break;
                }

                // lê título (verifica antes)
                if (!scanner.hasNextLine()) {
                    System.err.printf("Título ausente para ID %d. Encerrando leitura.%n", id);
                    break;
                }
                String title = scanner.nextLine().trim();

                // lê autor (verifica antes)
                if (!scanner.hasNextLine()) {
                    System.err.printf("Autor ausente para ID %d. Encerrando leitura.%n", id);
                    break;
                }
                String author = scanner.nextLine().trim();

                // insere na B-Tree
                Livro livro = new Livro(id, title, author);
                tree.remove(livro.getId());
                count++;

            }

        } catch (FileNotFoundException e) {
            System.err.printf("Arquivo nao encontrado: %s%n", filePath);
            return;
        }

        double endTime = System.nanoTime();
        double durationTotal = (endTime - startTime)/1_000_000;
        double durationMedia = (durationTotal/count);
        System.out.printf("\nTempo de remocao Total: %f ms\nTempo de remocao MEDIO: %f ms%n", durationTotal, durationMedia);
    }
}