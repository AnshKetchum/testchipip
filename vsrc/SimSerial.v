import "DPI-C" function int serial_tick
(
    input  bit     serial_out_valid,
    output bit     serial_out_ready,
    input  int     serial_out_bits,

    output bit     serial_in_valid,
    input  bit     serial_in_ready,
    output int     serial_in_bits
);

module SimSerial (
    input         clock,
    input         reset,
    input         serial_out_valid,
    output        serial_out_ready,
    input  [31:0] serial_out_bits,

    output        serial_in_valid,
    input         serial_in_ready,
    output [31:0] serial_in_bits,

    output        exit
);

    reg __out_valid;
    reg __in_ready;
    reg [31:0] __out_bits;

    bit __in_valid;
    bit __out_ready;
    int __in_bits;
    int __exit;

    reg __in_valid_reg;
    reg __out_ready_reg;
    reg [31:0] __in_bits_reg;
    reg __exit_reg;

    assign serial_in_valid  = __in_valid_reg;
    assign serial_in_bits   = __in_bits_reg;
    assign serial_out_ready = __out_ready_reg;

    // Evaluate the signals on the positive edge
    always @(posedge clock) begin
        if (reset) begin
            __in_valid = 0;
            __out_ready = 0;
            __exit = 0;
        end else begin
            __exit = serial_tick(
                __out_valid,
                __out_ready,
                __out_bits,
                __in_valid,
                __in_ready,
                __in_bits
            );
        end
    end

    // Update signals on the negative edge
    always @(negedge clock) begin
        __in_valid_reg <= __in_valid;
        __out_ready_reg <= __out_ready;
        __in_bits_reg <= __in_bits;
        __exit_reg <= __exit[0];
        __out_valid <= serial_out_valid;
        __in_ready <= serial_in_ready;
        __out_bits <= serial_out_bits;
    end

    assign exit = __exit_reg;

endmodule
