package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import java.time.LocalDateTime;

/**
 * Implementação da interface PickingMap.
 * Representa um mapa de recolha que contém as rotas geradas
 * para os veículos numa determinada data.
 */
public class PickingMapImpl implements PickingMap {

    private LocalDateTime date;
    private Route[] routes;

    /**
     * Construtor completo para criar um PickingMap com data e rotas específicas.
     *
     * @param date   A data de geração do mapa de recolha.
     * @param routes O conjunto de rotas atribuídas.
     */
    public PickingMapImpl(LocalDateTime date, Route[] routes) {
        this.date = date;
        this.routes = routes;
    }

    /**
     * Construtor sobrecarregado (Overloading).
     * Cria um PickingMap assumindo a data/hora exata do momento da sua criação.
     *
     * @param routes O conjunto de rotas atribuídas.
     */
    public PickingMapImpl(Route[] routes) {
        this.date = LocalDateTime.now();
        this.routes = routes;
    }

    /**
     * Devolve a data associada a este mapa de recolha.
     *
     * @return A data do mapa.
     */
    @Override
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Devolve as rotas definidas neste mapa de recolha.
     *
     * @return O array de rotas.
     */
    @Override
    public Route[] getRoutes() {
        return routes;
    }

    /**
     * Devolve uma representação textual do mapa de recolha.
     *
     * @return Uma String com a data e a quantidade de rotas.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PickingMap [Data: ").append(this.date)
          .append(", Total de Rotas: ").append(this.routes != null ? this.routes.length : 0)
          .append("]");
        return sb.toString();
    }
}
