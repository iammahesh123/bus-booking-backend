package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.BusCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusResponseDTO;
import com.mahesh.busbookingbackend.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus")
@Tag(name = "Bus Management", description = "APIs for managing bus information including creation, update, and retrieval of bus details")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new bus",
            description = "Registers a new bus in the system with all necessary details including capacity, amenities, and operator information",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bus details to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus created successfully",
                    content = @Content(schema = @Schema(implementation = BusResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Bus already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BusResponseDTO> createBus(@RequestBody BusCreateDTO busCreateDTO) {
        return ResponseEntity.ok(busService.createBus(busCreateDTO));
    }

    @PutMapping("/{bus_id}")
    @Operation(
            summary = "Update bus information",
            description = "Updates the details of an existing bus identified by its ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated bus details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus updated successfully",
                    content = @Content(schema = @Schema(implementation = BusResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BusResponseDTO> updateBus(
            @Parameter(description = "ID of the bus to be updated", required = true, example = "101")
            @PathVariable("bus_id") Long id,
            @RequestBody BusCreateDTO busCreateDTO) {
        return ResponseEntity.ok(busService.updateBus(id, busCreateDTO));
    }

    @GetMapping("/{bus_id}")
    @Operation(
            summary = "Get bus details by ID",
            description = "Retrieves complete information about a specific bus including its capacity, amenities, and current status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BusResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BusResponseDTO> getBus(
            @Parameter(description = "ID of the bus to retrieve", required = true, example = "101")
            @PathVariable("bus_id") Long id) {
        return ResponseEntity.ok(busService.getBus(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all buses",
            description = "Retrieves a list of all buses registered in the system with their basic information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of buses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BusResponseDTO[].class))),
            @ApiResponse(responseCode = "204", description = "No buses found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BusResponseDTO>> getBuses() {
        return ResponseEntity.ok(busService.getBuss());
    }

    @DeleteMapping("/{bus_id}")
    public ResponseEntity<HttpStatus> deleteBus(@PathVariable("bus_id") Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}