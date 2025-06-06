package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.dtos.BusRouteCreateDTO;
import com.mahesh.busbookingbackend.dtos.BusRouteResponseDTO;
import com.mahesh.busbookingbackend.service.BusRouteService;
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
@RequestMapping("/bus-route")
@Tag(name = "Bus Route Management", description = "APIs for managing bus routes, including creation, retrieval, update, and deletion of route information")
public class BusRouteController {
    private final BusRouteService busRouteService;

    public BusRouteController(BusRouteService busRouteService) {
        this.busRouteService = busRouteService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new bus route",
            description = "Creates a new bus route with the provided details including stops, timings, and other route information",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bus route details to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusRouteCreateDTO.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus route created successfully",
                    content = @Content(schema = @Schema(implementation = BusRouteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Route already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusRouteResponseDTO> createBusRoute(@RequestBody BusRouteCreateDTO busRouteCreateDTO) {
        return ResponseEntity.ok(busRouteService.createRoute(busRouteCreateDTO));
    }

    @PutMapping("/{bus_route_id}")
    @Operation(
            summary = "Update an existing bus route",
            description = "Updates the details of an existing bus route identified by its ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated bus route details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BusRouteCreateDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus route updated successfully",
                    content = @Content(schema = @Schema(implementation = BusRouteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus route not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusRouteResponseDTO> updateBusRoute(
            @Parameter(description = "ID of the bus route to be updated", required = true, example = "123")
            @PathVariable("bus_route_id") Long id,
            @RequestBody BusRouteCreateDTO busRouteCreateDTO) {
        return ResponseEntity.ok(busRouteService.updateRoute(id, busRouteCreateDTO));
    }

    @GetMapping("/{bus_route_id}")
    @Operation(
            summary = "Get bus route by ID",
            description = "Retrieves detailed information about a specific bus route including all stops and timings"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus route found",
                    content = @Content(schema = @Schema(implementation = BusRouteResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus route not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<BusRouteResponseDTO> getBusRoute(
            @Parameter(description = "ID of the bus route to be retrieved", required = true, example = "123")
            @PathVariable("bus_route_id") Long id) {
        return ResponseEntity.ok(busRouteService.getRoute(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all bus routes",
            description = "Retrieves a list of all available bus routes in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bus routes retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BusRouteResponseDTO[].class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "204", description = "No bus routes found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<List<BusRouteResponseDTO>> getAllBusRoutes() {
        return ResponseEntity.ok(busRouteService.getRoutes());
    }

    @DeleteMapping("/{bus_route_id}")
    @Operation(
            summary = "Delete a bus route",
            description = "Permanently removes a bus route from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bus route deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Bus route not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    private ResponseEntity<HttpStatus> deleteBusRoute(
            @Parameter(description = "ID of the bus route to be deleted", required = true, example = "123")
            @PathVariable("bus_route_id") Long id) {
        busRouteService.deleteRoute(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}