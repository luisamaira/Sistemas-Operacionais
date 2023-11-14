import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Pagina {
    int numero;
    int instrucao;
    int dado;
    int bitR;
    int bitM;
    int tempoEnvelhecimento;

    public Pagina(int numero, int instrucao, int dado, int bitR, int bitM, int tempoEnvelhecimento) {
        this.numero = numero;
        this.instrucao = instrucao;
        this.dado = dado;
        this.bitR = bitR;
        this.bitM = bitM;
        this.tempoEnvelhecimento = tempoEnvelhecimento;
    }
}

public class Simulador {
    private List<Integer> fifoQueue = new LinkedList<>();
    private List<Integer> relogioQueue = new LinkedList<>();
    private List<Integer> wsClockQueue = new LinkedList<>();
    private boolean[] secondChanceBits = new boolean[TAMANHO_RAM];
    private boolean[] bitReferencia = new boolean[TAMANHO_RAM];
    private int[] tempoEnvelhecimento = new int[TAMANHO_RAM];
    private static final int TAMANHO_RAM = 10;
    private static final int TAMANHO_SWAP = 100;
    private static final int NUM_INSTRUCOES = 1000;

    private Pagina[][] matrizRAM = new Pagina[TAMANHO_RAM][6];
    private Pagina[][] matrizSWAP = new Pagina[TAMANHO_SWAP][6];

    private Random random = new Random();

    private void nruAlgoritmo(int instrucao) {
        List<Pagina> paginasClasse0 = new ArrayList<>();
        List<Pagina> paginasClasse1 = new ArrayList<>();
        List<Pagina> paginasClasse2 = new ArrayList<>();
        List<Pagina> paginasClasse3 = new ArrayList<>();

        for (int i = 0; i < TAMANHO_RAM; i++) {
            Pagina pagina = matrizRAM[i][instrucao % 6];
            int classe = (pagina.bitR << 1) | pagina.bitM;
            switch (classe) {
                case 0:
                    paginasClasse0.add(pagina);
                    break;
                case 1:
                    paginasClasse1.add(pagina);
                    break;
                case 2:
                    paginasClasse2.add(pagina);
                    break;
                case 3:
                    paginasClasse3.add(pagina);
                    break;
            }
        }

        List<Pagina> paginasSelecionaveis = new ArrayList<>();
        if (!paginasClasse0.isEmpty())
            paginasSelecionaveis.addAll(paginasClasse0);
        else if (!paginasClasse1.isEmpty())
            paginasSelecionaveis.addAll(paginasClasse1);
        else if (!paginasClasse2.isEmpty())
            paginasSelecionaveis.addAll(paginasClasse2);
        else
            paginasSelecionaveis.addAll(paginasClasse3);

        Pagina paginaSubstituir = paginasSelecionaveis.get(random.nextInt(paginasSelecionaveis.size()));

        int indiceSubstituir = random.nextInt(TAMANHO_RAM);
        matrizRAM[indiceSubstituir][instrucao % 6] = matrizSWAP[paginaSubstituir.numero][instrucao % 6];
    }

    private void fifoAlgoritmo(int instrucao) {
        int instrucaoAtual = instrucao % 100;

        boolean presenteNaRAM = false;
        int indiceNaRAM = -1;
        for (int i = 0; i < TAMANHO_RAM; i++) {
            if (matrizRAM[i][0].instrucao == instrucaoAtual) {
                presenteNaRAM = true;
                indiceNaRAM = i;
                break;
            }
        }

        if (presenteNaRAM) {
            matrizRAM[indiceNaRAM][instrucao % 6].bitR = 1;

            if (random.nextInt(100) < 30) {
                matrizRAM[indiceNaRAM][instrucao % 6].dado += 1;
                matrizRAM[indiceNaRAM][instrucao % 6].bitM = 1;
            }
        } else {
            Pagina paginaASubstituir = matrizRAM[((LinkedList<Integer>) fifoQueue).poll()][instrucao % 6];

            matrizRAM[((LinkedList<Integer>) fifoQueue).peek()][instrucao % 6] = matrizSWAP[paginaASubstituir.numero][instrucao % 6];

            ((LinkedList<Integer>) fifoQueue).offer(((LinkedList<Integer>) fifoQueue).poll());
        }

        if (instrucao % 10 == 0) {
            for (int i = 0; i < TAMANHO_RAM; i++) {
                matrizRAM[i][instrucao % 6].bitR = 0;
            }
        }
    }

    private void fifoScAlgoritmo(int instrucao) {
        int instrucaoAtual = instrucao % 100;

        boolean presenteNaRAM = false;
        int indiceNaRAM = -1;
        for (int i = 0; i < TAMANHO_RAM; i++) {
            if (matrizRAM[i][0].instrucao == instrucaoAtual) {
                presenteNaRAM = true;
                indiceNaRAM = i;
                break;
            }
        }

        if (presenteNaRAM) {
            matrizRAM[indiceNaRAM][instrucao % 6].bitR = 1;

            if (random.nextInt(100) < 30) {
                matrizRAM[indiceNaRAM][instrucao % 6].dado += 1;
                matrizRAM[indiceNaRAM][instrucao % 6].bitM = 1;
            }
        } else {
            while (true) {
                int indiceSubstituir = ((LinkedList<Integer>) fifoQueue).poll();
                if (!secondChanceBits[indiceSubstituir]) {
                    Pagina paginaASubstituir = matrizRAM[indiceSubstituir][instrucao % 6];

                    matrizRAM[indiceSubstituir][instrucao % 6] = matrizSWAP[paginaASubstituir.numero][instrucao % 6];

                    ((LinkedList<Integer>) fifoQueue).offer(indiceSubstituir);

                    break;
                } else {

                    secondChanceBits[indiceSubstituir] = false;
                    ((LinkedList<Integer>) fifoQueue).offer(indiceSubstituir);
                }
            }
        }

        if (instrucao % 10 == 0) {
            for (int i = 0; i < TAMANHO_RAM; i++) {
                matrizRAM[i][instrucao % 6].bitR = 0;
            }
        }
    }

    private void relogioAlgoritmo(int instrucao) {
        int instrucaoAtual = instrucao % 100;

        boolean presenteNaRAM = false;
        int indiceNaRAM = -1;
        for (int i = 0; i < TAMANHO_RAM; i++) {
            if (matrizRAM[i][0].instrucao == instrucaoAtual) {
                presenteNaRAM = true;
                indiceNaRAM = i;
                break;
            }
        }

        if (presenteNaRAM) {

            matrizRAM[indiceNaRAM][instrucao % 6].bitR = 1;

            if (random.nextInt(100) < 30) {
                matrizRAM[indiceNaRAM][instrucao % 6].dado += 1;
                matrizRAM[indiceNaRAM][instrucao % 6].bitM = 1;
            }
        } else {

            while (true) {
                int indiceSubstituir = ((LinkedList<Integer>) relogioQueue).poll();
                if (bitReferencia[indiceSubstituir]) {

                    bitReferencia[indiceSubstituir] = false;
                    ((LinkedList<Integer>) relogioQueue).offer(indiceSubstituir);
                } else {
                    Pagina paginaASubstituir = matrizRAM[indiceSubstituir][instrucao % 6];

                    matrizRAM[indiceSubstituir][instrucao % 6] = matrizSWAP[paginaASubstituir.numero][instrucao % 6];

                    ((LinkedList<Integer>) relogioQueue).offer(indiceSubstituir);

                    break;
                }
            }
        }

        if (instrucao % 10 == 0) {
            for (int i = 0; i < TAMANHO_RAM; i++) {
                matrizRAM[i][instrucao % 6].bitR = 0;
            }
        }
    }

private void wsClockAlgoritmo(int instrucao) {
    int instrucaoAtual = instrucao % 100;

    boolean presenteNaRAM = false;
    int indiceNaRAM = -1;
    for (int i = 0; i < TAMANHO_RAM; i++) {
    if (matrizRAM[i][0].instrucao == instrucaoAtual) {
        presenteNaRAM = true;
        indiceNaRAM = i;
        break;
    }
    }

    if (presenteNaRAM) {

    matrizRAM[indiceNaRAM][instrucao % 6].bitR = 1;

    if (new Random().nextInt(100) < 30) {
        matrizRAM[indiceNaRAM][instrucao % 6].dado += 1;
        matrizRAM[indiceNaRAM][instrucao % 6].bitM = 1;
    }
    } else {

    while (true) {
        int indiceSubstituir = ((LinkedList<Integer>) wsClockQueue).poll();
        if (bitReferencia[indiceSubstituir]) {

        bitReferencia[indiceSubstituir] = false;
        ((LinkedList<Integer>) wsClockQueue).offer(indiceSubstituir);
        } else {
        Pagina paginaASubstituir = matrizRAM[indiceSubstituir][instrucao % 6];

        matrizRAM[indiceSubstituir][instrucao % 6] = matrizSWAP[paginaASubstituir.numero][instrucao % 6];

        ((LinkedList<Integer>) wsClockQueue).offer(indiceSubstituir);

        tempoEnvelhecimento[indiceSubstituir] = instrucao + new Random().nextInt(9900) + 100;

        break;
        }
    }
    }

    if (instrucao % 10 == 0) {
    for (int i = 0; i < TAMANHO_RAM; i++) {
        matrizRAM[i][instrucao % 6].bitR = 0;
    }
    }

    for (int i = 0; i < TAMANHO_RAM; i++) {
    if (tempoEnvelhecimento[i] <= instrucao) {

        wsClockQueue.remove(i);
    }
    }
}

public void simular() {

    for (int i = 0; i < NUM_INSTRUCOES; i++) {
    int instrucaoAtual = new Random().nextInt(100) + 1;

    }
}

public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.simular();
}