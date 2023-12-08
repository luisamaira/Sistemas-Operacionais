import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Filosofo extends Thread {
    private int id;
    private Lock garfoEsquerda;
    private Lock garfoDireita;

    public Filosofo(int id, Lock garfoEsquerda, Lock garfoDireita) {
        this.id = id;
        this.garfoEsquerda = garfoEsquerda;
        this.garfoDireita = garfoDireita;
    }

    private void pensar() throws InterruptedException {
        System.out.println("Filósofo " + id + " está pensando.");
        Thread.sleep(1000);
    }

    private void comer() throws InterruptedException {
        System.out.println("Filósofo " + id + " está comendo.");
        Thread.sleep(1000);
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();

                garfoEsquerda.lock();
                System.out.println("Filósofo " + id + " pegou o garfo esquerdo.");
                garfoDireita.lock();
                System.out.println("Filósofo " + id + " pegou o garfo direito.");

                comer();

                garfoEsquerda.unlock();
                System.out.println("Filósofo " + id + " liberou o garfo esquerdo.");
                garfoDireita.unlock();
                System.out.println("Filósofo " + id + " liberou o garfo direito.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class JantarDosFilosofos {
    public static void main(String[] args) {
        int numFilosofos = 5;
        Lock[] garfos = new Lock[numFilosofos];

        for (int i = 0; i < numFilosofos; i++) {
            garfos[i] = new ReentrantLock();
        }

        Thread[] filosofos = new Thread[numFilosofos];

        for (int i = 0; i < numFilosofos; i++) {
            filosofos[i] = new Filosofo(i, garfos[i], garfos[(i + 1) % numFilosofos]);
            filosofos[i].start();
        }
    }
}