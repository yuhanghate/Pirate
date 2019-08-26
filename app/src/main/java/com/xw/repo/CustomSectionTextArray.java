package com.xw.repo;

import android.util.SparseArray;
import androidx.annotation.NonNull;

/**
 * Customize the section texts under the track according to your demands by
 * call {@link #setCustomSectionTextArray(CustomSectionTextArray)}.
 */
public interface CustomSectionTextArray {
    /**
     * <p>
     * Customization goes here.
     * </p>
     * For example:
     * <pre> public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
     *     array.clear();
     *
     *     array.put(0, "worst");
     *     array.put(4, "bad");
     *     array.put(6, "ok");
     *     array.put(8, "good");
     *     array.put(9, "great");
     *     array.put(10, "excellent");
     * }</pre>
     *
     * @param sectionCount The section count of the {@code BubbleSeekBar}.
     * @param array        The section texts array which had been initialized already. Customize
     *                     the section text by changing one element's value of the SparseArray.
     *                     The index key ∈[0, sectionCount].
     * @return The customized section texts array. Can not be {@code null}.
     */
    @NonNull
    SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array);
}