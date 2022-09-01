package com.span.standings.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/***
 * POJO used for reading matches from input file.
 */
@Data
@AllArgsConstructor
public class Match {

  private String teamA;
  private int scoreA;
  private String teamB;
  private int scoreB;

}
