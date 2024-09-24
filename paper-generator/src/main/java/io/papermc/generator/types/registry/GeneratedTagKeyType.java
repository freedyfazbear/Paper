package io.papermc.generator.types.registry;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.papermc.generator.Main;
import io.papermc.generator.rewriter.types.registry.definition.RegistryEntry;
import io.papermc.generator.types.SimpleGenerator;
import io.papermc.generator.utils.Annotations;
import io.papermc.generator.utils.Formatting;
import io.papermc.generator.utils.Javadocs;
import io.papermc.generator.utils.experimental.SingleFlagHolder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static io.papermc.generator.utils.Annotations.EXPERIMENTAL_API_ANNOTATION;
import static io.papermc.generator.utils.Annotations.NON_NULL;
import static io.papermc.generator.utils.Annotations.experimentalAnnotations;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class GeneratedTagKeyType extends SimpleGenerator {

    private final RegistryEntry<?> entry;

    public GeneratedTagKeyType(final RegistryEntry<?> entry, final String packageName) {
        super(entry.keyClassName().concat("TagKeys"), packageName);
        this.entry = entry;
    }

    private MethodSpec.Builder createMethod(final TypeName returnType) {
        final TypeName keyType = TypeName.get(Key.class).annotated(NON_NULL);
        final boolean publicCreateKeyMethod = true; // tag lifecycle event exists

        final ParameterSpec keyParam = ParameterSpec.builder(keyType, "key", FINAL).build();
        final MethodSpec.Builder create = MethodSpec.methodBuilder("create")
            .addModifiers(publicCreateKeyMethod ? PUBLIC : PRIVATE, STATIC)
            .addParameter(keyParam)
            .addCode("return $T.create($T.$L, $N);", TagKey.class, RegistryKey.class, this.entry.registryKeyField(), keyParam)
            .returns(returnType.annotated(NON_NULL));
        if (publicCreateKeyMethod) {
            create.addAnnotation(EXPERIMENTAL_API_ANNOTATION); // TODO remove once not experimental
            create.addJavadoc(Javadocs.CREATED_TAG_KEY_JAVADOC, this.entry.apiClass(), this.entry.registryKey().location().toString());
        }
        return create;
    }

    private TypeSpec.Builder keyHolderType() {
        return classBuilder(this.className)
            .addModifiers(PUBLIC, FINAL)
            .addJavadoc(Javadocs.getVersionDependentClassHeader("tag keys", "{@link $T#$L}"), RegistryKey.class, this.entry.registryKeyField())
            .addAnnotations(Annotations.CLASS_HEADER)
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE)
                .build()
            );
    }

    @Override
    protected TypeSpec getTypeSpec() {
        final TypeName tagKeyType = ParameterizedTypeName.get(TagKey.class, this.entry.apiClass());

        final TypeSpec.Builder typeBuilder = this.keyHolderType();
        final MethodSpec.Builder createMethod = this.createMethod(tagKeyType);

        final Registry<?> registry = this.entry.registry();

        final AtomicBoolean allExperimental = new AtomicBoolean(true);
        registry.getTagNames().sorted(Formatting.alphabeticKeyOrder(tagKey -> tagKey.location().getPath())).forEach(tagKey -> {
            final String fieldName = Formatting.formatKeyAsField(tagKey.location().getPath());
            final FieldSpec.Builder fieldBuilder = FieldSpec.builder(tagKeyType, fieldName, PUBLIC, STATIC, FINAL)
                .initializer("$N(key($S))", createMethod.build(), tagKey.location().getPath())
                .addJavadoc(Javadocs.getVersionDependentField("{@code $L}"), "#" + tagKey.location());

            final String featureFlagName = Main.EXPERIMENTAL_TAGS.get(tagKey);
            if (featureFlagName != null) {
                fieldBuilder.addAnnotations(experimentalAnnotations(SingleFlagHolder.fromVanillaName(featureFlagName)));
            } else {
                allExperimental.set(false);
            }
            typeBuilder.addField(fieldBuilder.build());
        });
        if (allExperimental.get()) {
            typeBuilder.addAnnotation(EXPERIMENTAL_API_ANNOTATION);
            createMethod.addAnnotation(EXPERIMENTAL_API_ANNOTATION);
        } else {
            typeBuilder.addAnnotation(EXPERIMENTAL_API_ANNOTATION); // TODO experimental API
        }
        return typeBuilder.addMethod(createMethod.build()).build();
    }

    @Override
    protected JavaFile.Builder file(final JavaFile.Builder builder) {
        return builder.addStaticImport(Key.class, "key");
    }
}