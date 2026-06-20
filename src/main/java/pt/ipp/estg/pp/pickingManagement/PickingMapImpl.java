package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import java.time.LocalDateTime;

/**
 * Implementação da interface PickingMap.
 * Representa uma coleção de rotas planeadas para uma determinada data/hora.
 */
public class PickingMapImpl implements PickingMap {

    private LocalDateTime date;
    private Route[] routes;
    private int routeCount;
    private static final int DEFAULT_SIZE = 10;

    /**
     * Construtor para criar um PickingMap com uma data específica.
     *
     * @param date a data/hora do mapa de recolha
     */
    public PickingMapImpl(LocalDateTime date) {
        this.date = date;
        this.routes = new Route[DEFAULT_SIZE];
        this.routeCount = 0;
    }

    /**
     * Construtor para criar um PickingMap com uma data e um conjunto de rotas.
     *
     * @param date   a data/hora do mapa de recolha
     * @param routes as rotas a associar ao mapa
     */
    public PickingMapImpl(LocalDateTime date, Route[] routes) {
        this.date = date;
        if (routes != null) {
            this.routes = new Route[routes.length];
            System.arraycopy(routes, 0, this.routes, 0, routes.length);
            this.routeCount = routes.length;
        } else {
            this.routes = new Route[DEFAULT_SIZE];
            this.routeCount = 0;
        }
    }

    /**
     * Devolve a data/hora do mapa de recolha.
     *
     * @return a data do PickingMap
     */
    @Override
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Devolve uma cópia das rotas associadas a este mapa de recolha.
     *
     * @return cópia do array de rotas
     */
    @Override
    public Route[] getRoutes() {
        Route[] result = new Route[routeCount];
        System.arraycopy(routes, 0, result, 0, routeCount);
        return result;
    }

    /**
     * Adiciona uma rota ao mapa de recolha.
     *
     * @param route a rota a adicionar
     */
    public void addRoute(Route route) {
        if (route == null) {
            return;
        }

        if (routeCount == routes.length) {
            Route[] newRoutes = new Route[routes.length * 2];
            System.arraycopy(routes, 0, newRoutes, 0, routeCount);
            routes = newRoutes;
        }

        routes[routeCount] = route;
        routeCount++;
    }

    /**
     * Devolve a representação textual do mapa de recolha.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PickingMapImpl{date=").append(date)
                .append(", routeCount=").append(routeCount)
                .append("}");
        return sb.toString();
    }
}
