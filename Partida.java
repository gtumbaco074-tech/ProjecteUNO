package uno;

import uno.interficie.*;
import uno.logica.*;
import uno.logica.cartes.Carta;

import java.util.ArrayList;

public class Partida {
    private int NOMBRE_INICIAL_CARTES = 7;

    private int quantitatJugadors;

    Mazo mazo = new Mazo(this);
    Pilo pilo = new Pilo();
    OrdreJugadors ordreJugadors = new OrdreJugadors();

    public Mazo getMazo() {
        return mazo;
    }

    public OrdreJugadors getOrdreJugadors() {
        return ordreJugadors;
    }

    public void jugar() {
        preparar();
        boolean partidaAcabada = false;
        do {
            partidaAcabada = torn();
        } while (!partidaAcabada);
        UI.victoria(ordreJugadors.getJugadorActiu());
    }

    private void preparar() {
        mazo.barrejar();

        ArrayList<String> nomsJugadors = UI.demanarJugadors();
        quantitatJugadors = nomsJugadors.size();
        for (String nomJugador : nomsJugadors) {
            ordreJugadors.crearJugador(nomJugador);
        }
        ordreJugadors.barrejarOrdre();
        UI.mostrarOrdreJugadors(ordreJugadors.getJugadors());

        repartirCartes();

        Carta primeraCarta = mazo.agafarCarta();
        pilo.addCarta(primeraCarta);
    }

    private boolean torn() {
        // torn() torna true si la partida s'acaba. Torna false si continua.
        Jugador jugadorActiu = ordreJugadors.getJugadorActiu();
        UI.tornJugador(jugadorActiu, pilo);
        Carta cartaTirada = UI.demanarCarta(jugadorActiu, pilo);

        if (cartaTirada == null) {
            // jugador decideix robar
            if (mazo.esBuid()) mazo.reiniciar(pilo);

            // Robem la carta i la guardem en una variable per saber quina és
            jugadorActiu.robaCarta(mazo);

            // guardem la carta robada
            Carta cartaRobada = jugadorActiu.getCartes().get(jugadorActiu.nombreDeCartes() - 1);

            System.out.println("Has robat: ");
            UI.mostrarCarta(cartaRobada);

            // comprovem si la carta robada es pot tirar
            if (cartaRobada.sonCartesCompatibles(pilo.consultarCarta())) {
                System.out.print("Aquesta carta es pot tirar! La vols jugar? (s/n): ");
                String resposta = new java.util.Scanner(System.in).nextLine();

                if (resposta.equals("s")) {
                    jugadorActiu.tirarCarta(cartaRobada, pilo);
                    System.out.println("Has jugat la carta robada.");
                } else {
                    System.out.println("T'has guardat la carta i passes torn.");
                }
            } else {
                System.out.println("No pots jugar la carta robada. Passant torn...");
            }
        } else {
            jugadorActiu.tirarCarta(cartaTirada, pilo);
        }

        if (jugadorActiu.nombreDeCartes() <= 0) {
            return true;
        } else {
            ordreJugadors.passarTorn();
            return false;
        }
    }

    private void repartirCartes() {
        for (int i=0; i<quantitatJugadors*NOMBRE_INICIAL_CARTES; i++){
            ordreJugadors.getJugadorActiu().robaCarta(mazo);
            ordreJugadors.passarTorn();
        }
    }
}