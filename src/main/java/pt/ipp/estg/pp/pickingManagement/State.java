package pt.ipp.estg.pp.pickingManagement;

/**
 * Representa os possíveis estados de um veículo no sistema de picking.
 * Um veículo pode estar ativo ou desativado.
 */
public enum State {
    ACTIVE,
    DISABLE;

    /**
     * Retorna uma representação textual do estado.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (this == ACTIVE) {
            sb.append("Estado Ativo");
        } else if (this == DISABLE) {
            sb.append("Estado Desativado");
        } else {
            sb.append(super.toString());
        }
        return sb.toString();
    }
}
