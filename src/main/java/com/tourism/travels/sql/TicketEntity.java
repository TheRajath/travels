package com.tourism.travels.sql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ticket")
public class TicketEntity {

    @Id
    @Column(name = "ticket_id")
    private int ticketId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "package_id")
    private int packageId;

    @Column(name = "travel_date")
    private String travelDate;

    @Column(name = "total_members")
    private int totalMembers;

    @Column(name = "total_cost")
    private int totalCost;

}