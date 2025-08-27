package com.goldenraspberry.domain.port;

import com.goldenraspberry.domain.model.ProducerInterval;

import java.util.List;
import java.util.Map;

/**
 * Interface de servico para operacoes de intervalo de Producer
 * Este e um port na arquitetura hexagonal
 */
public interface ProducerIntervalService {

    /**
     * Obtem intervalos minimo e maximo entre vitorias consecutivas
     * @return Map contendo intervalos "min" e "max"
     */
    Map<String, List<ProducerInterval>> getMinMaxIntervals();

    /**
     * Obtem todos os intervalos para todos os Producer com multiplas vitorias
     * @return Map com nome do Producer como chave e intervalos como valor
     */
    Map<String, List<ProducerInterval>> getAllProducerIntervals();

    /**
     * Obtem intervalos para Producer especifico
     * @param producerName Nome do Producer
     * @return Lista de intervalos para o Producer
     */
    List<ProducerInterval> getIntervalsForProducer(String producerName);

    /**
     * Atualiza calculos de intervalo
     * Este metodo deve ser chamado quando os dados de Movie mudarem
     */
    void refreshIntervals();
}
