package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.Instruction;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.script.OpCode;
import io.neow3j.types.StackItemType;

import static io.neow3j.script.InteropService.SYSTEM_STORAGE_GET;

@Permission(contract = "*", methods = "*")
public class CustomScriptContract {

    static StorageContext ctx = Storage.getStorageContext();

    public static void put(ByteString key, int value) {
        Storage.put(ctx, key, value);
    }

    public static int get(ByteString key) throws Exception {
        try {
            return Helper.getIntOrThrow(ctx, key);
        } catch (Exception e) {
            throw new Exception("Couldn't find a value corresponding to key " + key.toString());
        }
    }

    static class Helper {

        @Instruction(opcode = OpCode.SWAP)
        @Instruction(interopService = SYSTEM_STORAGE_GET)
        @Instruction(opcode = OpCode.DUP)
        @Instruction(opcode = OpCode.ISNULL)
        @Instruction(opcode = OpCode.JMPIFNOT, operand = 0x16)
        @Instruction(
                opcode = OpCode.PUSHDATA1,
                operandPrefix = {0x11},
                operand = {0x22, 0x4e, 0x6f, 0x20, 0x65, 0x6e, 0x74, 0x72, 0x79, 0x20, 0x66, 0x6f,
                        0x75, 0x6e, 0x64, 0x2e, 0x22} // bytes for "no entry found" message
        )
        @Instruction(opcode = OpCode.THROW)
        @Instruction(opcode = OpCode.CONVERT, operand = StackItemType.INTEGER_CODE)
        public static native int getIntOrThrow(StorageContext context, ByteString key) throws Exception;

    }
}

