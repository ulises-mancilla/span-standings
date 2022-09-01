package com.span.standings.domain;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * POJO for generate standing by points.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
