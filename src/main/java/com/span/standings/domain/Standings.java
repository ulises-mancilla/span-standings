package com.span.standings.domain;

import java.util.Comparator;
import lombok.Data;

@Data
public class Standings implements Comparable<Standings> {

  private int rank;
  private String team;
  private int points;

  @Override
  public int compareTo(Standings o) {
    return Comparator.comparingInt(Standings::getPoints).reversed()
        .thenComparing(Standings::getTeam)
        .compare(this, o);
  }
}
