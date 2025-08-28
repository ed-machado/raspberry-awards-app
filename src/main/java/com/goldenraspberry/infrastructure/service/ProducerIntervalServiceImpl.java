package com.goldenraspberry.infrastructure.service;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.ProducerInterval;
import com.goldenraspberry.domain.port.MovieRepository;
import com.goldenraspberry.domain.port.ProducerIntervalService;
import com.goldenraspberry.domain.service.ProducerIntervalCalculator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servico de intervalos */
@Service
public class ProducerIntervalServiceImpl implements ProducerIntervalService {

  private final MovieRepository movieRepository;
  private final ProducerIntervalCalculator intervalCalculator;

  @Autowired
  public ProducerIntervalServiceImpl(
      MovieRepository movieRepository, ProducerIntervalCalculator intervalCalculator) {
    this.movieRepository = movieRepository;
    this.intervalCalculator = intervalCalculator;
  }

  @Override
  public Map<String, List<ProducerInterval>> getMinMaxIntervals() {
    List<Movie> allMovies = movieRepository.findAll();
    return intervalCalculator.findMinMaxIntervals(allMovies);
  }

  @Override
  public Map<String, List<ProducerInterval>> getAllProducerIntervals() {
    List<Movie> allMovies = movieRepository.findAll();
    return intervalCalculator.calculateAllIntervals(allMovies);
  }

  @Override
  public List<ProducerInterval> getIntervalsForProducer(String producerName) {
    List<Movie> producerMovies = movieRepository.findByProducerName(producerName);
    Map<String, List<ProducerInterval>> intervals =
        intervalCalculator.calculateAllIntervals(producerMovies);
    return intervals.getOrDefault(producerName, List.of());
  }

  @Override
  public void refreshIntervals() {
    // TODO Possivel limpeza de cache
  }
}
