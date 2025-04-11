/**
 * Thrown when a matrix oepration is applied to matrices of the wrong size. 
 *
 * You don't need to edit this. 
 */

public class UndefinedMatrixOpException extends Exception {
  

  public UndefinedMatrixOpException(String message, Matrix m1, Matrix m2) {
    super(message + "\nMatrix 1: " + m1 + "\n\nMatrix 2: " + m2);
  }

}