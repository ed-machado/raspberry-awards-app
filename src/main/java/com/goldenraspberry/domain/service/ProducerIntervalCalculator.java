package com.goldenraspberry.domain.service;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.ProducerInterval;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.common.annotation.DomainService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servico de dominio para calcular intervalos de Producer entre vitorias consecutivas
 */
@DomainService
public class ProducerIntervalCalculator {

    /**
     * Calcula intervalos para todos os Producer com multiplas vitorias
     * @param movies Lista de todos os filmes
     * @return Map com nome do Producer como chave e lista de intervalos como valor
     */
    public Map<String, List<ProducerInterval>> calculateAllIntervals(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Collections.emptyMap();
        }

        // Group winning movies by producer
        Map<String, List<Movie>> winsByProducer = groupWinningMoviesByProducer(movies);

        // Calculate intervals for each producer with multiple wins
        Map<String, List<ProducerInterval>> intervals = new HashMap<>();

        for (Map.Entry<String, List<Movie>> entry : winsByProducer.entrySet()) {
            String producerName = entry.getKey();
            List<Movie> producerWins = entry.getValue();

            if (producerWins.size() > 1) {
                List<ProducerInterval> producerIntervals = calculateIntervalsForProducer(
                    producerName, producerWins
                );
                if (!producerIntervals.isEmpty()) {
                    intervals.put(producerName, producerIntervals);
                }
            }
        }

        return intervals;
    }

    /**
     * Encontra intervalos minimo e maximo entre todos os Producer
     * @param movies Lista de todos os filmes
     * @return Map com chaves "min" e "max" contendo os respectivos intervalos
     */
    public Map<String, List<ProducerInterval>> findMinMaxIntervals(List<Movie> movies) {
        Map<String, List<ProducerInterval>> allIntervals = calculateAllIntervals(movies);

        if (allIntervals.isEmpty()) {
            return Map.of(
                "min", Collections.emptyList(),
                "max", Collections.emptyList()
            );
        }

        // Flatten all intervals
        List<ProducerInterval> flatIntervals = allIntervals.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        if (flatIntervals.isEmpty()) {
            return Map.of(
                "min", Collections.emptyList(),
                "max", Collections.emptyList()
            );
        }

        // Find min and max intervals
        int minInterval = flatIntervals.stream()
            .mapToInt(ProducerInterval::getInterval)
            .min()
            .orElse(0);

        int maxInterval = flatIntervals.stream()
            .mapToInt(ProducerInterval::getInterval)
            .max()
            .orElse(0);

        List<ProducerInterval> minIntervals = flatIntervals.stream()
            .filter(interval -> interval.getInterval() == minInterval)
            .collect(Collectors.toList());

        List<ProducerInterval> maxIntervals = flatIntervals.stream()
            .filter(interval -> interval.getInterval() == maxInterval)
            .collect(Collectors.toList());

        return Map.of(
            "min", minIntervals,
            "max", maxIntervals
        );
    }

    private Map<String, List<Movie>> groupWinningMoviesByProducer(List<Movie> movies) {
        Map<String, List<Movie>> winsByProducer = new HashMap<>();

        for (Movie movie : movies) {
            if (movie.isWinner()) {
                for (Producer producer : movie.getProducers()) {
                    winsByProducer.computeIfAbsent(producer.getName(), k -> new ArrayList<>())
                        .add(movie);
                }
            }
        }

        return winsByProducer;
    }

    private List<ProducerInterval> calculateIntervalsForProducer(String producerName, List<Movie> wins) {
        // Sort wins by year
        List<Movie> sortedWins = wins.stream()
            .sorted(Comparator.comparing(movie -> movie.getYear().getValue()))
            .collect(Collectors.toList());

        List<ProducerInterval> intervals = new ArrayList<>();
        Producer producer = new Producer(producerName);

        for (int i = 1; i < sortedWins.size(); i++) {
            Movie previousWin = sortedWins.get(i - 1);
            Movie currentWin = sortedWins.get(i);

            int interval = currentWin.getYear().getValue() - previousWin.getYear().getValue();

            ProducerInterval producerInterval = new ProducerInterval(
                producer,
                interval,
                previousWin.getYear(),
                currentWin.getYear()
            );

            intervals.add(producerInterval);
        }

        return intervals;
    }
}
