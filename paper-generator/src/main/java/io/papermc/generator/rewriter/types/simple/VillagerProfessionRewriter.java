package io.papermc.generator.rewriter.types.simple;

import io.papermc.generator.rewriter.types.registry.RegistryFieldRewriter;
import io.papermc.generator.utils.Formatting;
import io.papermc.typewriter.parser.Lexer;
import io.papermc.typewriter.parser.exception.ParserException;
import io.papermc.typewriter.parser.token.CharSequenceBlockToken;
import io.papermc.typewriter.parser.token.CharSequenceToken;
import io.papermc.typewriter.parser.token.Token;
import io.papermc.typewriter.parser.token.TokenType;
import io.papermc.typewriter.replace.SearchMetadata;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class VillagerProfessionRewriter extends RegistryFieldRewriter<VillagerProfession> {

    public VillagerProfessionRewriter() {
        super(Registries.VILLAGER_PROFESSION, "getProfession");
    }

    private static final Set<TokenType> FORMAT_TOKENS = EnumSet.of(
        TokenType.COMMENT,
        TokenType.SINGLE_COMMENT,
        TokenType.MARKDOWN_JAVADOC // for now ignore
    );

    private @MonotonicNonNull Map<String, List<String>> javadocsPerConstant;

    private Map<String, List<String>> parseConstantJavadocs(String content) {
        Map<String, List<String>> map = new HashMap<>();

        Lexer lex = new Lexer(content.toCharArray());
        @Nullable String constantName = null;
        @Nullable List<String> javadocs = null;
        boolean firstId = true;
        while (lex.canRead()) {
            Token token = lex.readToken();
            if (token.type() == TokenType.EOI) {
                break;
            }

            if (token.type() == TokenType.SECO) {
                if (constantName != null && javadocs != null) {
                    map.put(constantName, new ArrayList<>(javadocs));
                }
                firstId = true;
                constantName = null;
                javadocs = null;
                continue;
            }

            if (FORMAT_TOKENS.contains(token.type())) {
                continue;
            }

            if (token.type() == TokenType.LPAREN && constantName != null) {
                if (!this.skipClosure(lex)) {
                    return map;
                }
                continue;
            }

            if (token.type() == TokenType.JAVADOC) {
                javadocs = ((CharSequenceBlockToken) token).value();
            } else if (token.type() == TokenType.PUBLIC || token.type() == TokenType.STATIC || token.type() == TokenType.FINAL) {
                // should check duplicate per statement
                continue; // ignore
            } else if (token.type() == TokenType.IDENTIFIER && constantName == null) {
                if (firstId) {
                    @Nullable Token nextToken = this.skipTypeName(lex);
                    if (nextToken != null && nextToken.type() == TokenType.IDENTIFIER) {
                        token = nextToken;
                    }
                    firstId = false;
                }

                constantName = ((CharSequenceToken) token).value();
            }
        }

        return map;
    }

    private boolean skipClosure(Lexer lex) {
        int parenDepth = 1;
        while (lex.canRead()) {
            Token nestedToken = lex.readToken();
            if (nestedToken.type() == TokenType.EOI) {
                return false;
            }

            if (nestedToken.type() == TokenType.LPAREN) {
                parenDepth++;
            } else if (nestedToken.type() == TokenType.RPAREN) {
                parenDepth--;
            }

            if (parenDepth == 0) {
                break;
            }

            if (parenDepth < 0) {
                throw new ParserException("Unbalanced parenthesis", nestedToken);
            }
        }

        return true;
    }

    private @Nullable Token skipTypeName(Lexer lex) {
        boolean expectDot = true;
        while (lex.canRead()) {
            Token token = lex.readToken();
            if (token.type() == TokenType.EOI) {
                break;
            }

            if (FORMAT_TOKENS.contains(token.type()) || token.type() == TokenType.JAVADOC) {
                continue; // ignore intrusive comments inside the name
            }

            if (token.type() == (expectDot ? TokenType.DOT : TokenType.IDENTIFIER)) {
                expectDot = !expectDot;
            } else {
                return token;
            }
        }
        return null;
    }

    @Override
    protected void insert(final SearchMetadata metadata, final StringBuilder builder) {
        this.javadocsPerConstant = parseConstantJavadocs(metadata.replacedContent());
        super.insert(metadata, builder);
    }

    @Override
    protected void rewriteJavadocs(Holder.Reference<VillagerProfession> reference, String indent, StringBuilder builder) {
        String constantName = Formatting.formatKeyAsField(reference.key().location().getPath());
        if (this.javadocsPerConstant.containsKey(constantName)) {
            builder.append(indent).append("/**");
            builder.append('\n');
            for (String line : this.javadocsPerConstant.get(constantName)) {
                builder.append(indent).append(" * ").append(line);
                builder.append('\n');
            }
            builder.append(indent).append(" */");
            builder.append('\n');
        }
    }
}
