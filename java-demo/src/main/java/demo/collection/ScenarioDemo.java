package demo.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScenarioDemo
{

    public static void main(String[] args)
    {
        demoShuffle();
    }

    /**
     * 利用 Collections 里的API
     */
    private static void demoShuffle()
    {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 1; i <= 49; i++)
            numbers.add(i);

        Collections.shuffle(numbers);

        List<Integer> winningCombination = numbers.subList(0, 6);

        Collections.sort(winningCombination);
        System.out.println(winningCombination);
    }

}
