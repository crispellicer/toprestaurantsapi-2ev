package com.svalero.toprestaurantsapi.controller;

import com.svalero.toprestaurantsapi.domain.Shift;
import com.svalero.toprestaurantsapi.exception.ErrorMessage;
import com.svalero.toprestaurantsapi.exception.NumberFormatException;
import com.svalero.toprestaurantsapi.exception.ShiftNotFoundException;
import com.svalero.toprestaurantsapi.service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ShiftController {

    @Autowired
    private ShiftService shiftService;
    private final Logger logger = LoggerFactory.getLogger(ReserveController.class);

    @GetMapping("/shifts")
    public ResponseEntity<List<Shift>> getShifts(@RequestParam(name = "name", defaultValue = "") String name) {
        logger.debug("begin getShifts");
        if (name.equals("")) {
            logger.debug("end getShifts");
            return ResponseEntity.ok(shiftService.findAll());
        } else {
            logger.debug("end getShifts");
            return ResponseEntity.ok(shiftService.findByName(name));
        }
    }

    @GetMapping("/shifts/{stringId}")
    public ResponseEntity<Shift> getShift(@PathVariable String stringId) throws ShiftNotFoundException, NumberFormatException {
        logger.debug("begin getShift");
        if (!stringId.matches("[0-9]*")) {
            throw new NumberFormatException();
        }
        long id = Long.parseLong(stringId);
        Shift shift = shiftService.findById(id);
        logger.debug("end getShift");
        return ResponseEntity.ok(shift);
    }

    @PostMapping("/shifts")
    public ResponseEntity<Shift> addShift(@Valid @RequestBody Shift shift) {
        logger.debug("begin addShift");
        Shift newShift = shiftService.addShift(shift);
        logger.debug("end addShift");
        return ResponseEntity.status(HttpStatus.CREATED).body(newShift);
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable long id) throws ShiftNotFoundException {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/shifts/{id}")
    public ResponseEntity<Shift> modifyShift(@PathVariable long id, @Valid @RequestBody Shift shift) throws ShiftNotFoundException{
        logger.debug("begin modifyShift");
        Shift modifiedShift = shiftService.modifyShift(id, shift);
        logger.debug("end modifyShift");
        return ResponseEntity.status(HttpStatus.OK).body(modifiedShift);
    }

    @ExceptionHandler(ShiftNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleShiftNotFoundException(ShiftNotFoundException snfe) {
        logger.error(snfe.getMessage(), snfe);
        ErrorMessage errorMessage = new ErrorMessage(404, snfe.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorMessage> handleFormatNumberException(NumberFormatException nfe) {
        logger.error(nfe.getMessage(), nfe);
        ErrorMessage errorMessage = new ErrorMessage(400, nfe.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorMessage badRequestErrorMessage = new ErrorMessage(400, "Bad Request", errors);
        return new ResponseEntity<>(badRequestErrorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        logger.error(exception.getMessage(), exception);
        ErrorMessage errorMessage = new ErrorMessage(500, "Internal Server Error");
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

