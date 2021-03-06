/*
 * Copyright 2014 Tedroid developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.udlap.is522.tedroid.data.dao;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import mx.udlap.is522.tedroid.data.Score;
import mx.udlap.is522.tedroid.data.dao.impl.DAOFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class ScoreClassicDAOTest {

    private ScoreDAO scoreDAO;

    @Before
    public void setUp() throws Exception {
        scoreDAO = new DAOFactory(Robolectric.application).getScoreClassicDAO();
        assertThat(scoreDAO).isNotNull();
    }

    @Test
    public void shouldPersist() throws Exception {
        Score newScore = new Score();
        newScore.setLevel(5);
        newScore.setLines(54);
        newScore.setPoints(27442346);

        shouldPersist(newScore);
    }

    @Test
    public void shouldDeleteAll() throws Exception {
        Score score1 = new Score();
        score1.setLevel(5);
        score1.setLines(54);
        score1.setPoints(27442346);

        Score score2 = new Score();
        score2.setLevel(6);
        score2.setLines(65);
        score2.setPoints(453543678);

        Score score3 = new Score();
        score3.setLevel(2);
        score3.setLines(21);
        score3.setPoints(456456);

        shouldPersist(score1, score2, score3);

        scoreDAO.deleteAll();
        List<Score> all = scoreDAO.readAllOrderedByPointsDesc();
        assertThat(all).isNotNull().isEmpty();
    }

    @Test
    public void shouldSumCorrectly() throws Exception {
        Score score1 = new Score();
        score1.setLevel(5);
        score1.setLines(54);
        score1.setPoints(27442346);

        Score score2 = new Score();
        score2.setLevel(6);
        score2.setLines(65);
        score2.setPoints(453543678);

        Score score3 = new Score();
        score3.setLevel(2);
        score3.setLines(21);
        score3.setPoints(456456);

        shouldPersist(score1, score2, score3);

        int expectedPointsSum = score1.getPoints() + score2.getPoints() + score3.getPoints();
        int expectedLinesSum = score1.getLines() + score2.getLines() + score3.getLines();

        Map<String, Integer> sums = scoreDAO.readSumOfLinesAndPoints();

        assertThat(sums.get("points_sum")).isEqualTo(expectedPointsSum);
        assertThat(sums.get("lines_sum")).isEqualTo(expectedLinesSum);
    }

    private void shouldPersist(Score... scores) throws Exception {
        List<Score> all = scoreDAO.readAllOrderedByPointsDesc();
        assertThat(all).isNotNull().isEmpty();

        for (Score score : scores) {
            scoreDAO.save(score);
            Thread.sleep(1000L);
        }

        Arrays.sort(scores, new Comparator<Score>() {

            @Override
            public int compare(Score o1, Score o2) {
                return o1.getPoints() < o2.getPoints() ? 1 
                       : o1.getPoints() > o2.getPoints() ? -1 
                       : 0;
            }
        });

        all = scoreDAO.readAllOrderedByPointsDesc();
        assertThat(all).isNotNull().isNotEmpty().hasSize(scores.length).doesNotContainNull();

        for (int i = 0; i < scores.length; i++) {
            assertThat(all.get(i)).isNotNull().isLenientEqualsToByIgnoringFields(scores[i], "obtainedAt");
            assertThat(all.get(i).getObtainedAt()).isNotNull();
        }
    }
}