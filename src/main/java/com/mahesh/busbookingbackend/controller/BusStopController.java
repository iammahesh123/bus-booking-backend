package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.BusStopCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusStopResponseDTO;
import com.mahesh.busbookingbackend.service.BusStopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus-stop")
@Tag(name = "BusStop Management", description = "APIs for managing bus stops in the system")
public class BusStopController {

    private final BusStopService busStopService;

    public BusStopController(BusStopService busStopService) {
        this.busStopService = busStopService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new bus stop",
            description = "Creates a new bus stop with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bus stop details to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusStopCreateDTO.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus stop created successfully",
                    content = @Content(schema = @Schema(implementation = BusStopResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusStopResponseDTO> createBusStop(@RequestBody BusStopCreateDTO busStopCreateDTO) {
        return ResponseEntity.ok(busStopService.createBusStop(busStopCreateDTO));
    }

    @PutMapping("/{bus_stop_id}")
    @Operation(
            summary = "Update a bus stop",
            description = "Updates an existing bus stop with new details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated bus stop details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusStopCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus stop updated successfully",
                    content = @Content(schema = @Schema(implementation = BusStopResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus stop not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusStopResponseDTO> updateBusStop(
            @Parameter(description = "ID of the bus stop to be updated", required = true, example = "1")
            @PathVariable("bus_stop_id") Long busStopId,
            @RequestBody BusStopCreateDTO busStopCreateDTO) {
        return ResponseEntity.ok(busStopService.updateBusStop(busStopId, busStopCreateDTO));
    }

    @GetMapping("/{bus_stop_id}")
    @Operation(
            summary = "Get bus stop by ID",
            description = "Retrieves detailed information about a specific bus stop"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus stop found",
                    content = @Content(schema = @Schema(implementation = BusStopResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus stop not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusStopResponseDTO> getBusStop(
            @Parameter(description = "ID of the bus stop to be retrieved", required = true, example = "1")
            @PathVariable("bus_stop_id") Long busStopId) {
        return ResponseEntity.ok(busStopService.getBusStop(busStopId));
    }

    @GetMapping
    @Operation(
            summary = "Get all bus stops",
            description = "Retrieves a list of all available bus stops in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bus stops retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BusStopResponseDTO[].class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<List<BusStopResponseDTO>> getAllBusStops() {
        return ResponseEntity.ok(busStopService.getBusStops());
    }

    @DeleteMapping("/{bus_stop_id}")
    @Operation(
            summary = "Delete a bus stop",
            description = "Deletes a specific bus stop from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bus stop deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus stop not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<Void> deleteBusStop(
            @Parameter(description = "ID of the bus stop to be deleted", required = true, example = "1")
            @PathVariable("bus_stop_id") Long busStopId) {
        busStopService.deleteBusStop(busStopId);
        return ResponseEntity.noContent().build();
    }
}