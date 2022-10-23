package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.internal.CodeTree;
import net.frootloop.qa.parser.result.internal.Visibility;

public class ParsedMethod extends CodeTree {

    Visibility visibility;
    String methodName;
    String returnType;

    public ParsedMethod(BlockOfCode blockOfCode) {
        super(blockOfCode);
        this.methodName = StringParser.getDeclaredMethodName(blockOfCode.leadingStatement);
        this.visibility = StringParser.getDeclaredMethodVisibility(blockOfCode.leadingStatement);
        this.returnType = StringParser.getDeclaredMethodReturnType(blockOfCode.leadingStatement);
    }
}
