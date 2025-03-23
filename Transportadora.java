import java.io.*;

public class Transportadora {

    static class Pacote {
        String codigo;
        double valor, peso, volume;

        Pacote(String codigo, double valor, double peso, double volume) {
            this.codigo = codigo;
            this.valor = valor;
            this.peso = peso;
            this.volume = volume;
        }
    }

    static class Veiculo {
        String placa;
        double capacidadePeso, capacidadeVolume;

        Veiculo(String placa, double capacidadePeso, double capacidadeVolume) {
            this.placa = placa;
            this.capacidadePeso = capacidadePeso;
            this.capacidadeVolume = capacidadeVolume;
        }
    }

    public static void selecionarPacotes(Veiculo veiculo, Pacote[] pacotes, boolean[] selecionados, boolean[] alocado) {
        int n = pacotes.length;
        int W = (int) veiculo.capacidadePeso;
        int V = (int) veiculo.capacidadeVolume;
        double[][] dp = new double[W + 1][V + 1];
        boolean[][][] escolha = new boolean[n + 1][W + 1][V + 1];

        for (int i = 1; i <= n; i++) {
            int peso = (int) pacotes[i - 1].peso;
            int volume = (int) pacotes[i - 1].volume;
            double valor = pacotes[i - 1].valor;

            if (!alocado[i - 1]) {
                for (int w = W; w >= peso; w--) {
                    for (int v = V; v >= volume; v--) {
                        double novoValor = dp[w - peso][v - volume] + valor;
                        if (novoValor > dp[w][v] || (novoValor == dp[w][v] && (peso >= pacotes[i - 1].peso || volume >= pacotes[i - 1].volume))) {
                            dp[w][v] = novoValor;
                            escolha[i][w][v] = true;
                        }
                    }
                }
            }
        }

        int w = W, v = V;
        for (int i = n; i > 0; i--) {
            if (escolha[i][w][v]) {
                selecionados[i - 1] = true;
                w -= (int) pacotes[i - 1].peso;
                v -= (int) pacotes[i - 1].volume;
            }
        }
    }

    public static void main(String[] args) {
        try (BufferedReader scanner = new BufferedReader(new FileReader(args[0]));
             BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {

            int qtdVeiculos = Integer.parseInt(scanner.readLine().trim());
            Veiculo[] veiculos = new Veiculo[qtdVeiculos];
            for (int i = 0; i < qtdVeiculos; i++) {
                String[] veiculoInfo = scanner.readLine().trim().split(" ");
                veiculos[i] = new Veiculo(veiculoInfo[0], Double.parseDouble(veiculoInfo[1]), Double.parseDouble(veiculoInfo[2]));
            }

            int qtdPacotes = Integer.parseInt(scanner.readLine().trim());
            Pacote[] pacotes = new Pacote[qtdPacotes];
            for (int i = 0; i < qtdPacotes; i++) {
                String[] pacoteInfo = scanner.readLine().trim().split(" ");
                pacotes[i] = new Pacote(pacoteInfo[0], Double.parseDouble(pacoteInfo[1]), Double.parseDouble(pacoteInfo[2]), Double.parseDouble(pacoteInfo[3]));
            }

            boolean[] alocado = new boolean[pacotes.length];
            boolean primeiro = true;

            for (Veiculo veiculo : veiculos) {
                boolean[] selecionados = new boolean[pacotes.length];
                selecionarPacotes(veiculo, pacotes, selecionados, alocado);

                double totalValor = 0, totalPeso = 0, totalVolume = 0;
                StringBuilder pacotesCarregados = new StringBuilder();

                for (int i = 0; i < pacotes.length; i++) {
                    if (selecionados[i] && !alocado[i]) {
                        totalValor += pacotes[i].valor;
                        totalPeso += pacotes[i].peso;
                        totalVolume += pacotes[i].volume;
                        pacotesCarregados.append(pacotes[i].codigo).append(",");
                        alocado[i] = true;
                    }
                }

                if (pacotesCarregados.length() > 0) {
                    pacotesCarregados.setLength(pacotesCarregados.length() - 1);
                }

                if (!primeiro) {
                    writer.newLine();
                }
                primeiro = false;

                String valorFormatado = String.format("%.2f", totalValor).replace(",", ".");
                writer.write(String.format("[%s]R$%s,%dKG(%.0f%%),%dL(%.0f%%)->%s", veiculo.placa, valorFormatado, (int) totalPeso, (totalPeso / veiculo.capacidadePeso) * 100, (int) totalVolume, (totalVolume / veiculo.capacidadeVolume) * 100, pacotesCarregados.toString()));
            }

            StringBuilder pendentes = new StringBuilder();
            double valorPendente = 0, pesoPendente = 0, volumePendente = 0;

            for (int i = 0; i < pacotes.length; i++) {
                if (!alocado[i]) {
                    valorPendente += pacotes[i].valor;
                    pesoPendente += pacotes[i].peso;
                    volumePendente += pacotes[i].volume;
                    pendentes.append(pacotes[i].codigo).append(",");
                }
            }

            if (pendentes.length() > 0) {
                pendentes.setLength(pendentes.length() - 1);
                writer.newLine();
                String valorPendenteFormatado = String.format("%.2f", valorPendente).replace(",", ".");
                writer.write(String.format("PENDENTE:R$%s,%dKG,%dL->%s", valorPendenteFormatado, (int) pesoPendente, (int) volumePendente, pendentes.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
