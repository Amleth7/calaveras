import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Juego {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        System.out.print("¿Indique número de participantes que tomarán la partida?: ");
        int numParticipantes = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        String[] nombres = new String[numParticipantes];
        int[] puntajes = new int[numParticipantes];

        for (int i = 0; i < numParticipantes; i++) {
            System.out.print("Por favor indique nombre de participante #" + (i + 1) + ": ");
            nombres[i] = scanner.nextLine();
        }

        int jugador = 0;

        while (true) {
            System.out.println(dtf.format(LocalDateTime.now()) + " - " + obtenerPuntajesFormateados(nombres, puntajes));
            System.out.println();
            System.out.println(dtf.format(LocalDateTime.now()) + " - Ahora es el turno de " + nombres[jugador]);
            int resultadoTurno = jugarTurno();

            if (resultadoTurno == 0) {
                System.out.println(nombres[jugador] + " ha perdido todos los puntos acumulados. Siguiente turno...");
                puntajes[jugador] = 0;
            } else {
                puntajes[jugador] += resultadoTurno;
            }

            if (puntajes[jugador] >= 13) {
                System.out.println("¡" + nombres[jugador] + " ha obtenido 13 o más puntos! Todos los demás participantes tienen un turno adicional.");
                for (int j = 0; j < numParticipantes; j++) {
                    if (j != jugador) {
                        puntajes[j] += jugarTurno();
                    }
                }
                determinarGanador(nombres, puntajes);
                System.out.println("¡Gracias por jugar!");
                scanner.close();
                System.exit(0);
            }
            jugador++;
            if (jugador >= numParticipantes)
                jugador = 0;
        }
    }

    private static String obtenerPuntajesFormateados(String[] nombres, int[] puntajes) {
        StringBuilder resultado = new StringBuilder("PUNTAJES: ");
        for (int i = 0; i < nombres.length; i++) {
            resultado.append(nombres[i]).append(" = ").append(puntajes[i]);
            if (i < nombres.length - 1) {
                resultado.append(", ");
            }
        }
        return resultado.toString();
    }

    private static int jugarTurno() {
        int calaverasTurno = 0;
        int estrellasTurno = 0;
        boolean turnoActivo = true;

        while (turnoActivo) {
            List<Dado> dadosLanzados = obtenerDadosAleatorios();
            int[] resultados = new int[dadosLanzados.size()];

            System.out.println("Obteniendo tres dados del cubo...");
            System.out.println("Lanzando los tres dados...");

            for (int i = 0; i < dadosLanzados.size(); i++) {
                resultados[i] = dadosLanzados.get(i).lanzar();
            }

            imprimirJugada(dadosLanzados, resultados);

            for (int resultado : resultados) {
                if (resultado == Dado.ESTRELLA) estrellasTurno++;
                else if (resultado == Dado.CALAVERA) calaverasTurno++;
            }

            System.out.println("Estrellas: " + estrellasTurno + " Calaveras: " + calaverasTurno);

            if (calaverasTurno >= 3) {
                System.out.println("¡Tres calaveras! Perdiste todos los puntos acumulados en este turno.");
                return 0;
            } else {
                turnoActivo = preguntarContinuarTurno();
            }
        }

        return estrellasTurno;
    }

    private static boolean preguntarContinuarTurno() {
        while (true) {
            System.out.print("¿Quieres jugar de nuevo [S/N]? ");
            char respuesta = scanner.next().toUpperCase().charAt(0);
            if (respuesta == 'S') {
                return true;
            } else if (respuesta == 'N') {
                return false;
            } else {
                System.out.println("Entrada inválida. Por favor, ingrese 'S' para sí o 'N' para no.");
            }
        }
    }

    private static void determinarGanador(String[] nombres, int[] puntajes) {
        int maxPuntaje = -1;
        String ganador = "";

        for (int i = 0; i < puntajes.length; i++) {
            if (puntajes[i] > maxPuntaje) {
                maxPuntaje = puntajes[i];
                ganador = nombres[i];
            }
        }

        System.out.println("¡Ganó " + ganador + " con un puntaje de " + maxPuntaje + "!");
    }

    private static List<Dado> obtenerDadosAleatorios() {
        List<Dado> dados = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int tipoDado = random.nextInt(3);
            switch (tipoDado) {
                case 0 -> dados.add(new DadoOro());
                case 1 -> dados.add(new DadoPlata());
                case 2 -> dados.add(new DadoBronce());
            }
        }
        return dados;
    }

    private static void imprimirJugada(List<Dado> dadosLanzados, int[] resultados) {
        String[] calavera = {
                "+-----------+",
                "|    _    |",
                "|   /   \\   |",
                "|  |() ()|  |",
                "|   \\   /   |",
                "|    vvv    |",
                "+-----------+"
        };
        String[] estrella = {
                "+-----------+",
                "|     .     |",
                "|    ,0,    |",
                "| 'oo000oo' |",
                "|   ´000´   |",
                "|   0' '0   |",
                "+-----------+"
        };
        String[] comodin = {
                "+-----------+",
                "|           |",
                "|           |",
                "|     ?     |",
                "|           |",
                "|           |",
                "+-----------+"
        };

        String[][] caras = {calavera, estrella, comodin};

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < dadosLanzados.size(); j++) {
                switch (resultados[j]) {
                    case Dado.CALAVERA -> System.out.print(caras[0][i] + " ");
                    case Dado.ESTRELLA -> System.out.print(caras[1][i] + " ");
                    case Dado.COMODIN -> System.out.print(caras[2][i] + " ");
                }
            }
            System.out.println();
        }

        for (Dado dado : dadosLanzados) {
            if (dado instanceof DadoOro) {
                System.out.print("    ORO     ");
            } else if (dado instanceof DadoPlata) {
                System.out.print("   PLATA    ");
            } else if (dado instanceof DadoBronce) {
                System.out.print("   BRONCE   ");
            }
        }
        System.out.println();
    }
}

abstract class Dado {
    static final int CALAVERA = -1;
    static final int ESTRELLA = 1;
    static final int COMODIN = 0;
    protected static Random random = new Random();

    abstract int lanzar();
}

class DadoOro extends Dado {
    private static final int[] CARAS = {ESTRELLA, COMODIN, ESTRELLA, CALAVERA, ESTRELLA, ESTRELLA};

    @Override
    int lanzar() {
        return CARAS[random.nextInt(CARAS.length)];
    }
}

class DadoPlata extends Dado {
    private static final int[] CARAS = {CALAVERA, CALAVERA, CALAVERA, CALAVERA, COMODIN, ESTRELLA};

    @Override
    int lanzar() {
        return CARAS[random.nextInt(CARAS.length)];
    }
}

class DadoBronce extends Dado {
    private static final int[] CARAS = {CALAVERA, ESTRELLA, COMODIN, COMODIN, ESTRELLA, CALAVERA};

    @Override
    int lanzar() {
        return CARAS[random.nextInt(CARAS.length)];
    }
}