package org.deeplearning4j.datasets.iterator.file;

import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Iterate over a directory (and optionally subdirectories) containing a number of {@link MultiDataSet} objects that have
 * previously been saved to files with {@link MultiDataSet#save(File)}.<br>
 * This iterator supports the following (optional) features, depending on the constructor used:<br>
 * - Recursive listing of all files (i.e., include files in subdirectories)<br>
 * - Filtering based on a set of file extensions (if null, no filtering - assume all files are saved MultiDataSet objects)<br>
 * - Randomization of iteration order (default enabled, if a {@link Random} instance is provided<br>
 * - Combining and splitting of MultiDataSets (disabled by default, or if batchSize == -1. If enabled, MultiDataSet
 * objects will be split or combined as required to ensure the specified minibatch size is returned. In other words, the
 * saved MultiDataSet objects can have a different number of examples vs. those returned by the iterator.<br>
 *
 * @author Alex BLack
 */
public class FileMultiDataSetIterator extends BaseFileIterator<MultiDataSet, MultiDataSetPreProcessor> implements MultiDataSetIterator {


    /**
     * Create a FileMultiDataSetIterator with the following default settings:<br>
     * - Recursive: files in subdirectories are included<br>
     * - Randomization: order of examples is randomized with a random RNG seed<br>
     * - Batch size: default (as in the stored DataSets - no splitting/combining)<br>
     * - File extensions: no filtering - all files in directory are assumed to be a DataSet<br>
     *
     * @param rootDir Root directory containing the DataSet objects
     */
    public FileMultiDataSetIterator(File rootDir) {
        this(rootDir, true, new Random(), -1, (String[]) null);
    }

    /**
     * Create a FileMultiDataSetIterator with the specified batch size, and the following default settings:<br>
     * - Recursive: files in subdirectories are included<br>
     * - Randomization: order of examples is randomized with a random RNG seed<br>
     * - File extensions: no filtering - all files in directory are assumed to be a DataSet<br>
     *
     * @param rootDir   Root directory containing the saved DataSet objects
     * @param batchSize Batch size. If > 0, DataSets will be split/recombined as required. If <= 0, DataSets will
     *                  simply be loaded and returned unmodified
     */
    public FileMultiDataSetIterator(File rootDir, int batchSize) {
        this(rootDir, batchSize, (String[]) null);
    }

    /**
     * Create a FileMultiDataSetIterator with filtering based on file extensions, and the following default settings:<br>
     * - Recursive: files in subdirectories are included<br>
     * - Randomization: order of examples is randomized with a random RNG seed<br>
     * - Batch size: default (as in the stored DataSets - no splitting/combining)<br>
     *
     * @param rootDir         Root directory containing the saved DataSet objects
     * @param validExtensions May be null. If non-null, only files with one of the specified extensions will be used
     */
    public FileMultiDataSetIterator(File rootDir, String... validExtensions) {
        super(rootDir, -1, validExtensions);
    }

    /**
     * Create a FileMultiDataSetIterator with the specified batch size, filtering based on file extensions, and the
     * following default settings:<br>
     * - Recursive: files in subdirectories are included<br>
     * - Randomization: order of examples is randomized with a random RNG seed<br>
     *
     * @param rootDir         Root directory containing the saved DataSet objects
     * @param batchSize       Batch size. If > 0, DataSets will be split/recombined as required. If <= 0, DataSets will
     *                        simply be loaded and returned unmodified
     * @param validExtensions May be null. If non-null, only files with one of the specified extensions will be used
     */
    public FileMultiDataSetIterator(File rootDir, int batchSize, String... validExtensions) {
        super(rootDir, batchSize, validExtensions);
    }

    /**
     * Create a FileMultiDataSetIterator with all settings specified
     *
     * @param rootDir         Root directory containing the saved DataSet objects
     * @param recursive       If true: include files in subdirectories
     * @param rng             May be null. If non-null, use this to randomize order
     * @param batchSize       Batch size. If > 0, DataSets will be split/recombined as required. If <= 0, DataSets will
     *                        simply be loaded and returned unmodified
     * @param validExtensions May be null. If non-null, only files with one of the specified extensions will be used
     */
    public FileMultiDataSetIterator(File rootDir, boolean recursive, Random rng, int batchSize, String... validExtensions) {
        super(rootDir, recursive, rng, batchSize, validExtensions);
    }

    @Override
    protected MultiDataSet load(File f) {
        MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet();
        try {
            mds.load(f);
        } catch (IOException e) {
            throw new RuntimeException("Error loading MultiDataSet from file: " + f, e);
        }
        return mds;
    }

    @Override
    protected int sizeOf(MultiDataSet of) {
        return of.getFeatures(0).size(0);
    }

    @Override
    protected List<MultiDataSet> split(MultiDataSet toSplit) {
        return toSplit.asList();
    }

    @Override
    public MultiDataSet merge(List<MultiDataSet> toMerge) {
        return org.nd4j.linalg.dataset.MultiDataSet.merge(toMerge);
    }

    @Override
    protected void applyPreprocessor(MultiDataSet toPreProcess) {
        if (preProcessor != null) {
            preProcessor.preProcess(toPreProcess);
        }
    }

    @Override
    public MultiDataSet next(int num) {
        throw new UnsupportedOperationException("Not supported for this iterator");
    }
}
