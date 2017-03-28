package machinelearning.agent;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.RegOptimizer;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;

import java.util.List;

/**
 * A concrete implementation of {@link AbstractWekaForecastingModel}
 * specifically for the {@link WekaForecaster} used with the {@link SMOreg}
 * classifier.
 * 
 * @author Benedict Wilkins
 *
 */
public class SMOregForecastingModel extends AbstractWekaForecastingModel {

  private SMOreg model;
  private WekaForecaster forecaster;
  private String endTrainingTime = null;

  /**
   * Constructor. See {@link SMOreg} for details on parameters.
   * 
   * @param complexity
   *          {@link SMOreg} parameter
   * @param kernel
   *          {@link SMOreg} parameter
   * @param optimizer
   *          {@link SMOreg} parameter
   */
  public SMOregForecastingModel(Double complexity, Kernel kernel,
      RegOptimizer optimizer) {
    model = new SMOreg();
    model.setDebug(true);
    if (complexity != null) {
      model.setC(complexity);
    }
    if (kernel != null) {
      model.setKernel(kernel);
    }
    if (optimizer != null) {
      model.setRegOptimizer(optimizer);
    }
    try {
      this.forecaster = new WekaForecaster();
      forecaster.setFieldsToForecast(DataFrameMetaTimeValue.getInstance()
          .getHeaders()[DataFrameMetaTimeValue.getValueColumnIndex()]);
      forecaster.setBaseForecaster(model);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void trainModel(DataFrame data) {
    try {
      endTrainingTime = data.getRow(data.getNumRows() - 1).getValue(0,
          String.class);
      Instances instances = convertToTimeSeriesInstances(data);
      forecaster.buildForecaster(instances, System.out);
      forecaster.primeForecaster(instances);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public DataFrame getForecasts(int numsteps) {
    try {
      List<List<NumericPrediction>> forecasts = forecaster.forecast(numsteps,
          System.out);
      return toDataFrame(forecasts, endTrainingTime);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public SMOreg getModel() {
    return model;
  }

}
