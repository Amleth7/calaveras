import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Juego {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        System.out.print("¿Indique número de participantes que tomarán la partida?: ");
        int numParticipantes = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        Jugador[] jugadores = new Jugador[numParticipantes];

        for (int i = 0; i < numParticipantes; i++) {
            System.out.print("Por favor indique nombre de participante #" + (i + 1) + ": ");
            jugadores[i] = new Jugador(scanner.nextLine());
        }

        while (true) {

            for(Jugador jugador: jugadores) {

                System.out.println(dtf.format(LocalDateTime.now()) + " - " + obtenerPuntajesFormateados(jugadores));
                System.out.println();
                System.out.println(dtf.format(LocalDateTime.now()) + " - Ahora es el turno de " + jugador.getNombre());
                int resultadoTurno = jugarTurno();

                if (resultadoTurno == 0) {
                    System.out.println(jugador.getNombre() + " ha perdido todos los puntos acumulados. Siguiente turno...");
                } else {
                    jugador.incrementarPuntaje(resultadoTurno);
                }

                if (jugador.getPuntaje() >= 13) {
                    System.out.println("¡" + jugador + " ha obtenido 13 o más puntos! Todos los demás participantes tienen un turno adicional.");
                    for(Jugador otroJugador: jugadores) {
                        if (otroJugador != jugador) {
                            otroJugador.incrementarPuntaje(jugarTurno());
                        }
                    }
                    determinarGanador(jugadores);
                    System.out.println("¡Gracias por jugar!");
                    scanner.close();
                    System.exit(0);
                }
            }
        }
    }

    private static String obtenerPuntajesFormateados(Jugador[] jugadores) {
        StringBuilder resultado = new StringBuilder("PUNTAJES: ");
        for (int i = 0; i < jugadores.length; i++) {
            resultado.append(jugadores[i].toString());
            if (i < jugadores.length - 1) {
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
            List<CaraDado> resultados = new ArrayList<>(dadosLanzados.size());

            System.out.println("Obteniendo tres dados del cubo...");
            System.out.println("Lanzando los tres dados...");

            for (Dado dado : dadosLanzados) {
                resultados.add(dado.lanzar());
            }

            imprimirJugada(dadosLanzados, resultados);

            for (CaraDado resultado : resultados) {
                if (resultado instanceof Estrella) estrellasTurno++;
                else if (resultado instanceof Calavera) calaverasTurno++;
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

    private static void determinarGanador(Jugador[] jugadores) {
        int maxPuntaje = -1;
        String ganador = "";

        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i].getPuntaje() > maxPuntaje) {
                maxPuntaje = jugadores[i].getPuntaje();
                ganador = jugadores[i].getNombre();
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

    private static void imprimirJugada(List<Dado> dados, List<CaraDado> caraDados) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < caraDados.size(); j++) {
                System.out.print(caraDados.get(j).figura()[i] + " ");
            }
            System.out.println();
        }

        for (Dado dado : dados) {
            System.out.print(dado.etiqueta() + " ");
        }
        System.out.println();
    }
}

class Jugador {

    private String nombre = null;
    private int puntaje = 0;

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public void incrementarPuntaje(int puntaje) {
        this.puntaje += puntaje;
    }

    @Override
    public String toString() {
        return nombre + " = " + puntaje;
    }
}

abstract class Dado {

    private static Random random = new Random();

    protected abstract CaraDado[] caras();

    public CaraDado lanzar() {
        return caras()[random.nextInt(caras().length)];
    }

    public abstract String etiqueta();
}

class DadoOro extends Dado {
    protected CaraDado[] caras() {
        return new CaraDado[]{
                new Estrella(),
                new Comodin(),
                new Estrella(),
                new Calavera(),
                new Estrella(),
                new Estrella()
        };
    }

    @Override
    public String etiqueta() {
        return "     ORO     ";
    }

    ;
}

class DadoPlata extends Dado {
    protected CaraDado[] caras() {
        return new CaraDado[]{
                new Calavera(),
                new Calavera(),
                new Calavera(),
                new Calavera(),
                new Comodin(),
                new Estrella()
        };
    }

    @Override
    public String etiqueta() {
        return "    PLATA    ";
    }
}

class DadoBronce extends Dado {
    protected CaraDado[] caras() {
        return new CaraDado[]{
                new Calavera(),
                new Estrella(),
                new Comodin(),
                new Comodin(),
                new Estrella(),
                new Calavera()
        };
    }

    @Override
    public String etiqueta() {
        return "    BRONCE   ";
    }
}

abstract class CaraDado {
    abstract int puntaje();

    abstract String[] figura();
}

final class Estrella extends CaraDado {

    @Override
    int puntaje() {
        return 1;
    }

    @Override
    String[] figura() {
        return new String[]{
                "+-----------+",
                "|     .     |",
                "|    ,0,    |",
                "| 'oo000oo' |",
                "|   ´000´   |",
                "|   0' '0   |",
                "+-----------+"
        };
    }
}

final class Calavera extends CaraDado {

    @Override
    int puntaje() {
        return -1;
    }

    @Override
    String[] figura() {
        return new String[]{
                "+-----------+",
                "|    _    |",
                "|   /   \\   |",
                "|  |() ()|  |",
                "|   \\   /   |",
                "|    vvv    |",
                "+-----------+"
        };
    }
}

final class Comodin extends CaraDado {
    @Override
    int puntaje() {
        return 0;
    }

    @Override
    String[] figura() {
        return new String[]{
                "+-----------+",
                "|           |",
                "|           |",
                "|     ?     |",
                "|           |",
                "|           |",
                "+-----------+"
        };
    }
}