package Carshowroom;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.owlike.genson.Genson;
 
 
@Contract(
        name = "CarShowroom",
        info = @Info(
                title = "CarShowroom contract",
                description = "A Sample Car transfer chaincode example",
                version = "0.0.1-SNAPSHOT"))
 
 
@Default
public final class CarTransfer implements ContractInterface {
 
	private final Genson genson = new Genson();
	private enum CarShowroomErrors {
	        Car_NOT_FOUND,
	        Car_ALREADY_EXISTS
	    }
	
	
	/**
     * Add some initial properties to the ledger
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {
    	
        ChaincodeStub stub= ctx.getStub();
        
        Car Car = new Car("1", "Maruti","Mark","6756");
        
        String CarState = genson.serialize(Car);
        
        stub.putStringState("1", CarState);
    }
    
    
    /**
     * Add new Car on the ledger.
     *
     * @param ctx the transaction context
     * @param id the key for the new Car
     * @param model the model of the new Car
     * @param ownername the owner of the new Car
     * @param value the value of the new Car
     * @return the created Car
     */
	
    @Transaction()
    public Car addNewCar(final Context ctx, final String id, final String model,
            final String ownername, final String value) {
        
    	ChaincodeStub stub = ctx.getStub();
 
        String CarState = stub.getStringState(id);
        
        if (!CarState.isEmpty()) {
            String errorMessage = String.format("Car %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CarShowroomErrors.Car_ALREADY_EXISTS.toString());
        }
        
        Car Car = new Car(id, model, ownername,value);
        
        CarState = genson.serialize(Car);
        
        stub.putStringState(id, CarState); 
        
        return Car;
    }
 
 
    	/**
	     * Retrieves a Car based upon Car Id from the ledger.
	     *
	     * @param ctx the transaction context
	     * @param id the key
	     * @return the Car found on the ledger if there was one
	     */
    	@Transaction()
	    public Car queryCarById(final Context ctx, final String id) {
	        ChaincodeStub stub = ctx.getStub();
	        String CarState = stub.getStringState(id);
 
	        if (CarState.isEmpty()) {
	            String errorMessage = String.format("Car %s does not exist", id);
	            System.out.println(errorMessage);
	            throw new ChaincodeException(errorMessage, CarShowroomErrors.Car_NOT_FOUND.toString());
	        }
	        
	        Car Car = genson.deserialize(CarState, Car.class);
	        return Car;
	    }
    	
        /**
   	     * Changes the owner of a Car on the ledger.
   	     *
   	     * @param ctx the transaction context
   	     * @param id the key
   	     * @param newOwner the new owner
   	     * @return the updated Car
   	     */
   	    @Transaction()
   	    public Car changeCarOwnership(final Context ctx, final String id, final String newCarOwner) {
   	        ChaincodeStub stub = ctx.getStub();
 
   	        String CarState = stub.getStringState(id);
 
   	        if (CarState.isEmpty()) {
   	            String errorMessage = String.format("Car %s does not exist", id);
   	            System.out.println(errorMessage);
   	            throw new ChaincodeException(errorMessage, CarShowroomErrors.Car_NOT_FOUND.toString());
   	        }
 
   	        Car Car = genson.deserialize(CarState, Car.class);
 
   	        Car newCar = new Car(Car.getId(), Car.getModel(), newCarOwner, Car.getValue());
   	        
   	        String newCarState = genson.serialize(newCar);
   	        
   	        stub.putStringState(id, newCarState);
 
   	        return newCar;
   	    } 
}
