package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.Instruction;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.constants.InteropService;
import io.neow3j.devpack.constants.OpCode;
import io.neow3j.devpack.constants.StackItemType;

@Permission(contract = "*", methods = "*")
public class CustomScriptContract {

    public static void put(ByteString key, int value) {
        Storage.put(Storage.getStorageContext(), key, value);
    }

    public static int get(ByteString key) throws Exception {
        try {
            return Helper.getIntOrThrow(Storage.getReadOnlyContext(), key);
        } catch (Exception e) {
            throw new Exception("Couldn't find a value corresponding to key " + key.toString());
        }
    }

    static class Helper {

        @Instruction(opcode = OpCode.SWAP)
        @Instruction(interopService = InteropService.SYSTEM_STORAGE_GET)
        @Instruction(opcode = OpCode.DUP)
        @Instruction(opcode = OpCode.ISNULL)
        @Instruction(opcode = OpCode.JMPIFNOT, operand = 0x16)
        @Instruction(
                opcode = OpCode.PUSHDATA1,
                operandPrefix = {0x11},
                operand = {0x22, 0x4e, 0x6f, 0x20, 0x65, 0x6e, 0x74, 0x72, 0x79, 0x20, 0x66, 0x6f, 0x75, 0x6e, 0x64,
                        0x2e, 0x22} // bytes for "no entry found" message
                )
        @Instruction(opcode = OpCode.THROW)
        @Instruction(opcode = OpCode.CONVERT, operand = StackItemType.INTEGER)
        public static native int getIntOrThrow(StorageContext context, ByteString key) throws Exception;

    }

}
